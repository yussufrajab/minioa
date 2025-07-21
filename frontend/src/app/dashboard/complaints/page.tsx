
'use client';
import { PageHeader } from '@/components/shared/page-header';
import { Card, CardContent, CardDescription, CardHeader, CardTitle, CardFooter } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { useAuth } from '@/hooks/use-auth';
import { ROLES, EMPLOYEES } from '@/lib/constants';
import React, { useState, useEffect } from 'react';
import { standardizeComplaintFormatting } from '@/ai/flows/complaint-rewriter';
import type { Role as UserRole } from '@/lib/types';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { toast } from '@/hooks/use-toast';
import { Loader2, Eye, Edit3, Send, CheckCircle, XCircle, Info, MessageSquarePlus, Edit, Filter, Phone, Users, FileText } from 'lucide-react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter, DialogClose } from '@/components/ui/dialog';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { format, parseISO } from 'date-fns';

const COMPLAINT_TYPES = [
  "Unyanyasaji",
  "Kuchelewa Kupandishwa Cheo",
  "Kuchelewa Kuthibitishwa",
  "Uongozi Mbaya",
  "Uamuzi Usio wa Haki",
  "Usalama Mahali Kwa Ajira",
  "Ubaguzi",
  "Mengineyo",
];

const phoneValidation = z.string({ required_error: "Namba ya simu inahitajika."})
  .length(10, { message: "Namba ya simu lazima iwe na tarakimu 10 haswa." })
  .startsWith("0", { message: "Namba ya simu lazima ianze na '0'." })
  .regex(/^[0-9]+$/, { message: "Herufi zisizo sahihi. Tarakimu tu ndizo zinazoruhusiwa." });

const complaintSchema = z.object({
  complaintType: z.string().min(1, "Aina ya malalamiko inahitajika."),
  subject: z.string().min(5, "Kichwa lazima kiwe na angalau herufi 5.").max(100, "Kichwa lazima kiwe na herufi 100 au chini yake."),
  complaintText: z.string().min(20, "Maelezo ya malalamiko lazima yawe na angalau herufi 20."),
  complainantPhoneNumber: phoneValidation,
  nextOfKinPhoneNumber: phoneValidation,
  evidence: z.custom<FileList | null>().optional(),
});

type ComplaintFormValues = z.infer<typeof complaintSchema>;

interface SubmittedComplaint {
  id: string;
  employeeId?: string | null;
  employeeName: string; 
  zanId?: string | null; 
  department?: string | null;
  cadre?: string | null;
  complaintType: string;
  subject: string;
  details: string; 
  complainantPhoneNumber?: string;
  nextOfKinPhoneNumber?: string;
  submissionDate: string;
  status: string;
  attachments?: string[]; 
  officerComments?: string | null;
  internalNotes?: string | null;
  assignedOfficerRole?: string | null;
  reviewStage: string;
  rejectionReason?: string | null;
  reviewedBy?: UserRole | null;
}

export default function ComplaintsPage() {
  const { role, user } = useAuth();
  const [rewrittenComplaint, setRewrittenComplaint] = useState<string | null>(null);
  const [isRewriting, setIsRewriting] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  const [complaints, setComplaints] = useState<SubmittedComplaint[]>([]);
  const [selectedComplaint, setSelectedComplaint] = useState<SubmittedComplaint | null>(null);
  const [isDetailsModalOpen, setIsDetailsModalOpen] = useState(false);
  
  const [isActionModalOpen, setIsActionModalOpen] = useState(false); 
  const [officerActionComment, setOfficerActionComment] = useState('');
  const [officerInternalNote, setOfficerInternalNote] = useState(''); 
  const [actionType, setActionType] = useState<"resolve" | "request_info" | null>(null);

  const [isRejectionModalOpen, setIsRejectionModalOpen] = useState(false); 
  const [rejectionReasonInput, setRejectionReasonInput] = useState('');
  const [currentRequestToAction, setCurrentRequestToAction] = useState<SubmittedComplaint | null>(null);

  // Employee provide more info modal
  const [isProvideInfoModalOpen, setIsProvideInfoModalOpen] = useState(false);
  const [additionalInfo, setAdditionalInfo] = useState('');
  const [selectedComplaintForInfo, setSelectedComplaintForInfo] = useState<SubmittedComplaint | null>(null);


  const form = useForm<ComplaintFormValues>({
    resolver: zodResolver(complaintSchema),
    defaultValues: {
      complaintType: "",
      subject: "",
      complaintText: "",
      complainantPhoneNumber: "",
      nextOfKinPhoneNumber: "",
      evidence: null,
    },
  });
  
  useEffect(() => {
    const fetchComplaints = async () => {
        if (!user || !role) return;
        setIsLoading(true);
        try {
            const response = await fetch(`/api/complaints?userId=${user.id}&userRole=${role}`);
            if (!response.ok) {
                throw new Error('Failed to fetch complaints');
            }
            const data = await response.json();
            setComplaints(data);
        } catch (error) {
            toast({ title: "Hitilafu", description: "Imeshindwa kupakia malalamiko.", variant: "destructive" });
        } finally {
            setIsLoading(false);
        }
    };
    fetchComplaints();
  }, [user, role]);


  const handleStandardizeComplaint = async () => {
    const complaintText = form.getValues("complaintText");
    if (!complaintText) {
      toast({ title: "Hitilafu ya Kuandika Upya", description: "Tafadhali ingiza maelezo ya lalamiko lako kwanza.", variant: "destructive" });
      return;
    }
    setIsRewriting(true);
    setRewrittenComplaint(null);
    try {
      const result = await standardizeComplaintFormatting({ complaintText });
      setRewrittenComplaint(result.rewrittenComplaint);
      form.setValue("complaintText", result.rewrittenComplaint, { shouldValidate: true }); 
      toast({ title: "Lalamiko Limeboreshwa", description: "AI imeandika upya maelezo ya lalamiko lako kwa uwazi na utimilifu. Yamesasishwa kwenye fomu." });
    } catch (error) {
      console.error("AI Rewrite Error:", error);
      toast({ title: "Kuandika Upya Kumeshindikana", description: "Imeshindwa kuboresha lalamiko kwa kutumia AI.", variant: "destructive" });
    } finally {
      setIsRewriting(false);
    }
  };
  
  const onEmployeeSubmit = async (data: ComplaintFormValues) => {
    if (!user) {
      toast({title: "Hitilafu", description: "Maelezo ya mtumiaji hayajapatikana.", variant: "destructive"});
      return;
    }
    setIsSubmitting(true);
    
    // In a real app, file upload would be handled here, e.g., to a cloud storage service.
    // For this prototype, we'll just pass the file names.
    const attachmentNames = data.evidence ? Array.from(data.evidence).map(file => file.name) : [];
    
    try {
        const response = await fetch('/api/complaints', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ ...data, attachments: attachmentNames, complainantId: user.id }),
        });
        
        if (!response.ok) {
            throw new Error('Failed to submit complaint');
        }

        const newComplaint = await response.json();
        
        setComplaints(prev => [newComplaint, ...prev]);
        toast({ title: "Lalamiko Limewasilishwa", description: "Lalamiko lako limewasilishwa kwa mafanikio." });
        form.reset();
        setRewrittenComplaint(null);
    } catch (error) {
        toast({ title: "Kuwasilisha Kumeshindikana", description: "Hitilafu imetokea wakati wa kuwasilisha lalamiko lako.", variant: "destructive" });
    } finally {
        setIsSubmitting(false);
    }
  };
  
  const updateComplaintState = (updatedComplaint: SubmittedComplaint) => {
      setComplaints(prev => prev.map(c => c.id === updatedComplaint.id ? { ...c, ...updatedComplaint } : c));
  };

  const handleUpdateComplaint = async (complaintId: string, payload: any) => {
      try {
          const response = await fetch(`/api/complaints/${complaintId}`, {
              method: 'PUT',
              headers: { 'Content-Type': 'application/json' },
              body: JSON.stringify(payload)
          });
          if (!response.ok) throw new Error('Failed to update complaint');
          const updatedComplaint = await response.json();
          updateComplaintState(updatedComplaint);
          return updatedComplaint;
      } catch (error) {
          toast({ title: "Kusasisha Kumeshindikana", description: "Imeshindwa kusasisha lalamiko.", variant: "destructive" });
          return null;
      }
  };


  const handleInitialAction = async (complaintId: string, action: 'forward' | 'reject_initial') => {
    const complaint = complaints.find(c => c.id === complaintId);
    if (!complaint) return;

    if (action === 'reject_initial') {
      setCurrentRequestToAction(complaint);
      setRejectionReasonInput(''); 
      setIsRejectionModalOpen(true);
    } else if (action === 'forward') {
      const payload = { 
        status: "Awaiting Commission Review", 
        reviewStage: 'commission_review', 
        reviewedById: user?.id 
      };
      const updated = await handleUpdateComplaint(complaintId, payload);
      if (updated) {
        toast({ title: "Lalamiko Limepelekwa", description: `Lalamiko ${complaintId} limepelekwa kwa Ukaguzi wa Tume.` });
      }
    }
  };
  
  const handleRejectionSubmit = async () => {
    if (!currentRequestToAction || !rejectionReasonInput.trim()) {
      toast({ title: "Hitilafu ya Kukataa", description: "Sababu ya kukataa inahitajika.", variant: "destructive" });
      return;
    }
    const { id, employeeName } = currentRequestToAction;
    const rejectedByRole = role === ROLES.DO ? "DO" : "HHRMD";

    const payload = {
        status: `Rejected by ${rejectedByRole} - Awaiting HRO/Submitter Action`,
        rejectionReason: rejectionReasonInput,
        reviewStage: 'initial',
        reviewedById: user?.id
    };

    const updated = await handleUpdateComplaint(id, payload);
    if (updated) {
        toast({ title: "Lalamiko Limekataliwa", description: `Lalamiko ${id} la ${employeeName} limekataliwa.`, variant: 'destructive' });
        setIsRejectionModalOpen(false);
        setCurrentRequestToAction(null);
        setRejectionReasonInput('');
    }
  };

  const handleCommissionDecision = async (complaintId: string, decision: 'commission_approve' | 'commission_reject') => {
    const finalStatus = decision === 'commission_approve' ? "Resolved - Approved by Commission" : "Resolved - Rejected by Commission";
    const payload = { status: finalStatus, reviewStage: 'completed', reviewedById: user?.id };
    
    const updated = await handleUpdateComplaint(complaintId, payload);
    if (updated) {
       toast({ title: `Uamuzi wa Tume: ${decision === 'commission_approve' ? 'Umekubaliwa' : 'Umekataliwa'}`, description: `Lalamiko ${complaintId} limesasishwa.` });
    }
  };

  const openActionModal = (complaint: SubmittedComplaint, type: "resolve" | "request_info") => {
    setSelectedComplaint(complaint);
    setActionType(type); 
    setOfficerActionComment(complaint.officerComments || '');
    setOfficerInternalNote(complaint.internalNotes || '');
    setIsActionModalOpen(true);
  };
  
  const handleOfficerSubmitLegacyAction = async () => {
    if (!selectedComplaint || !actionType || !officerActionComment.trim()) {
      toast({title: "Hitilafu ya Hatua", description: "Maoni ya afisa yanahitajika.", variant: "destructive"});
      return;
    }

    let newStatus: string = selectedComplaint.status;
    let toastMessage = "";

    if (actionType === 'resolve') {
      newStatus = "Resolved - Pending Employee Confirmation";
      toastMessage = `Lalamiko ${selectedComplaint.id} limewekwa kama Limetatuliwa.`;
    } else if (actionType === 'request_info') {
      newStatus = "Awaiting More Information";
      toastMessage = `Maelezo zaidi yameombwa kwa lalamiko ${selectedComplaint.id}.`;
    }
    
    const payload = {
        status: newStatus,
        officerComments: officerActionComment,
        internalNotes: officerInternalNote,
        assignedOfficerRole: role,
        reviewStage: 'completed',
        reviewedById: user?.id
    };

    const updated = await handleUpdateComplaint(selectedComplaint.id, payload);

    if (updated) {
      toast({ title: "Hatua Imechukuliwa", description: toastMessage });
      setIsActionModalOpen(false);
      setSelectedComplaint(null);
      setOfficerActionComment('');
      setOfficerInternalNote('');
      setActionType(null);
    }
  };

  const handleEmployeeConfirmOutcome = async (complaintId: string) => {
    const payload = { status: "Closed - Satisfied" };
    const updated = await handleUpdateComplaint(complaintId, payload);
    if (updated) {
      toast({title: "Lalamiko Limefungwa", description: "Asante kwa maoni yako."});
    }
  };

  const openProvideInfoModal = (complaint: SubmittedComplaint) => {
    setSelectedComplaintForInfo(complaint);
    setAdditionalInfo('');
    setIsProvideInfoModalOpen(true);
  };

  const handleProvideAdditionalInfo = async () => {
    if (!selectedComplaintForInfo || !additionalInfo.trim()) {
      toast({title: "Hitilafu", description: "Tafadhali toa maelezo ya ziada yaliyoombwa.", variant: "destructive"});
      return;
    }

    // Update the complaint with additional information and change status back to "Under Review"
    const payload = {
      status: "Under Review - Additional Information Provided",
      officerComments: `${selectedComplaintForInfo.officerComments || ''}\n\n--- Additional Information from Employee ---\n${additionalInfo}`,
      reviewStage: 'initial'
    };

    const updated = await handleUpdateComplaint(selectedComplaintForInfo.id, payload);
    if (updated) {
      toast({title: "Maelezo Yametolewa", description: "Maelezo yako ya ziada yamewasilishwa kwa ukaguzi."});
      setIsProvideInfoModalOpen(false);
      setSelectedComplaintForInfo(null);
      setAdditionalInfo('');
    }
  };
  
  // Both DO and HHRMD should see all complaints assigned to either role
  const complaintsForOfficerReview = complaints.filter(c => 
    (role === ROLES.DO || role === ROLES.HHRMD) && 
    (c.assignedOfficerRole === ROLES.DO || c.assignedOfficerRole === ROLES.HHRMD)
  );
  const employeeSubmittedComplaints = complaints.filter(c => c.employeeId === user?.employeeId);

  return (
    <div>
      <PageHeader title="Usimamizi wa Malalamiko" description="Wasilisha, tazama, na simamia malalamiko ya wafanyakazi." />
      
      {role === ROLES.EMPLOYEE && (
        <>
        <Card className="shadow-lg mb-8">
            <CardHeader>
                <CardTitle>Malalamiko Yangu Niliyowasilisha</CardTitle>
                <CardDescription>Fuatilia hali ya malalamiko uliyowasilisha.</CardDescription>
            </CardHeader>
            <CardContent>
                {isLoading ? (
                    <div className="flex justify-center items-center h-40">
                        <Loader2 className="h-8 w-8 animate-spin" />
                    </div>
                ) : employeeSubmittedComplaints.length > 0 ? (
                    employeeSubmittedComplaints.map(complaint => (
                        <div key={complaint.id} className="mb-4 border p-4 rounded-md space-y-3 shadow-sm bg-background hover:shadow-md transition-shadow">
                            <div className="flex justify-between items-start">
                                <div>
                                    <h3 className="font-semibold text-base">{complaint.subject}</h3>
                                    <p className="text-sm text-muted-foreground">Aina: {complaint.complaintType} | Tarehe ya Kuwasilisha: {format(parseISO(complaint.submissionDate), 'PPP')}</p>
                                </div>
                                <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                                    complaint.status === "Closed - Satisfied" ? "bg-green-100 text-green-700" :
                                    complaint.status.startsWith("Resolved") ? "bg-blue-100 text-blue-700" :
                                    complaint.status.startsWith("Rejected") ? "bg-red-100 text-red-700" :
                                    complaint.status === "Awaiting More Information" ? "bg-yellow-100 text-yellow-700" :
                                    complaint.status === "Under Review - Additional Information Provided" ? "bg-purple-100 text-purple-700" :
                                    "bg-gray-100 text-gray-700"
                                }`}>{complaint.status}</span>
                            </div>
                            <p className="text-sm "><strong className="text-muted-foreground">Maelezo:</strong> {complaint.details.substring(0,150)}{complaint.details.length > 150 ? '...' : ''}</p>
                            {complaint.officerComments && (complaint.status.startsWith("Resolved") || complaint.status.startsWith("Rejected by") || complaint.status === "Awaiting More Information") && (
                                <Card className="mt-2 bg-secondary/30">
                                    <CardHeader className="pb-1 pt-2">
                                        <CardTitle className="text-sm font-medium">Maoni ya Afisa / Sababu:</CardTitle>
                                    </CardHeader>
                                    <CardContent className="pb-2">
                                        <p className="text-sm text-muted-foreground">{complaint.officerComments}</p>
                                    </CardContent>
                                </Card>
                            )}
                            {complaint.rejectionReason && (complaint.status.startsWith("Rejected by") || complaint.status === "Resolved - Rejected by Commission") && (
                                <Card className="mt-2 bg-red-50 border-red-200">
                                    <CardHeader className="pb-1 pt-2">
                                        <CardTitle className="text-sm font-medium text-red-700">Sababu ya Kukataliwa:</CardTitle>
                                    </CardHeader>
                                    <CardContent className="pb-2">
                                        <p className="text-sm text-red-600">{complaint.rejectionReason}</p>
                                    </CardContent>
                                </Card>
                            )}
                            {(complaint.status === "Resolved - Pending Employee Confirmation" || complaint.status === "Rejected - Pending Employee Confirmation" || complaint.status === "Resolved - Approved by Commission" || complaint.status === "Resolved - Rejected by Commission") && (
                                <div className="mt-3 pt-3 border-t">
                                    <Button size="sm" onClick={() => handleEmployeeConfirmOutcome(complaint.id)} className="bg-green-600 hover:bg-green-700 text-white">
                                        <CheckCircle className="mr-2 h-4 w-4"/>
                                        Thibitisha Matokeo na Funga Lalamiko
                                    </Button>
                                </div>
                            )}
                             {complaint.status === "Awaiting More Information" && (
                                <div className="mt-3 pt-3 border-t">
                                    <p className="text-sm text-amber-700 font-medium mb-3">Afisa mkaguzi ameomba maelezo zaidi. Tafadhali kagua maoni yao na toa maelezo ya ziada hapa chini.</p>
                                    <Button size="sm" onClick={() => openProvideInfoModal(complaint)} className="bg-blue-600 hover:bg-blue-700 text-white">
                                        <MessageSquarePlus className="mr-2 h-4 w-4"/>
                                        Toa Maelezo ya Ziada
                                    </Button>
                                </div>
                            )}
                        </div>
                    ))
                ) : (
                    <p className="text-muted-foreground">Bado hujawasilisha malalamiko yoyote.</p>
                )}
            </CardContent>
        </Card>

        <Card className="mb-6 shadow-lg">
          <CardHeader>
            <CardTitle>Wasilisha Lalamiko Jipya</CardTitle>
            <CardDescription>Eleza lalamiko lako kwa uwazi. Unaweza kutumia zana ya AI kusaidia kuboresha maandishi katika sehemu ya maelezo.</CardDescription>
          </CardHeader>
          <CardContent>
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onEmployeeSubmit)} className="space-y-6">
                <FormField
                  control={form.control}
                  name="complaintType"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Aina ya Lalamiko *</FormLabel>
                      <Select onValueChange={field.onChange} defaultValue={field.value}>
                        <FormControl>
                          <SelectTrigger>
                            <SelectValue placeholder="Chagua aina ya lalamiko lako" />
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent>
                          {COMPLAINT_TYPES.map(type => (
                            <SelectItem key={type} value={type}>{type}</SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="subject"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Kichwa / Mada *</FormLabel>
                      <FormControl>
                        <Input placeholder="Fupisha lalamiko lako kwa ufupi" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="complaintText"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Maelezo ya Kina *</FormLabel>
                      <FormControl>
                        <Textarea placeholder="Toa maelezo kamili ya lalamiko lako..." {...field} rows={6} />
                      </FormControl>
                       <p className="text-sm text-muted-foreground pt-1">
                        Unaweza kutumia zana ya AI hapo chini kusaidia kuboresha maelezo yako kwa uwazi na utimilifu.
                      </p>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="complainantPhoneNumber"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center"><Phone className="mr-2 h-4 w-4 text-primary"/>Namba Yako ya Simu ya Sasa *</FormLabel>
                      <FormControl>
                        <Input type="tel" placeholder="Ingiza namba yako ya simu, mfano: 0777123456" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="nextOfKinPhoneNumber"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center"><Users className="mr-2 h-4 w-4 text-primary"/>Namba ya Simu ya Mlezi / Mdhamini *</FormLabel>
                      <FormControl>
                        <Input type="tel" placeholder="Ingiza namba ya simu ya mlezi, mfano: 0777123456" {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={form.control}
                  name="evidence"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Pakia Vifungo (Si Lazima, PDF/Picha)</FormLabel>
                      <FormControl>
                        <Input 
                          type="file" 
                          multiple 
                          accept=".pdf,.png,.jpg,.jpeg"
                          onChange={(e) => field.onChange(e.target.files)}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                 <div className="flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2 pt-2">
                  <Button type="button" variant="outline" onClick={handleStandardizeComplaint} disabled={isRewriting}>
                    {isRewriting ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : <Edit3 className="mr-2 h-4 w-4"/>}
                    Boresha Maelezo kwa AI
                  </Button>
                  <Button type="submit" disabled={isRewriting || isSubmitting}>
                    {isSubmitting ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : <Send className="mr-2 h-4 w-4"/>}
                    Wasilisha Lalamiko
                  </Button>
                </div>
              </form>
            </Form>
            {rewrittenComplaint && !isRewriting && (
              <Card className="mt-4 bg-secondary/50">
                <CardHeader>
                  <CardTitle className="text-base">Pendekezo la AI (Limesasishwa kwenye Fomu)</CardTitle>
                </CardHeader>
                <CardContent className="text-sm">
                  <p>{rewrittenComplaint}</p>
                </CardContent>
              </Card>
            )}
          </CardContent>
        </Card>

        </>
      )}

      {(role === ROLES.DO || role === ROLES.HHRMD) && (
         <Card className="shadow-lg">
          <CardHeader>
            <CardTitle>Kagua Malalamiko Yaliyowasilishwa</CardTitle>
            <CardDescription>Kagua, chukua hatua, au omba maelezo zaidi kuhusu malalamiko ya wafanyakazi yaliyokabidhiwa kwako.</CardDescription>
          </CardHeader>
          <CardContent>
            {isLoading ? (
                 <div className="flex justify-center items-center h-40">
                    <Loader2 className="h-8 w-8 animate-spin" />
                </div>
            ) : complaintsForOfficerReview.length > 0 ? (
              complaintsForOfficerReview.map((complaint) => (
                <div key={complaint.id} className="mb-4 border p-4 rounded-md space-y-2 shadow-sm bg-background hover:shadow-md transition-shadow">
                  <div className="flex justify-between items-start">
                    <h3 className="font-semibold text-base">{complaint.subject} (Type: {complaint.complaintType})</h3>
                     <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                        complaint.status === "Submitted" ? "bg-orange-100 text-orange-700" : 
                        complaint.status === "Under Review" ? "bg-blue-100 text-blue-700" :
                        complaint.status === "Under Review - Additional Information Provided" ? "bg-purple-100 text-purple-700" :
                        complaint.status === "Awaiting Commission Review" ? "bg-purple-100 text-purple-700" :
                        complaint.status.startsWith("Resolved") ? "bg-green-100 text-green-700" :
                        complaint.status.startsWith("Rejected") ? "bg-red-100 text-red-700" :
                        "bg-gray-100 text-gray-700"
                    }`}>{complaint.status}</span>
                  </div>
                  <p className="text-sm text-muted-foreground">
                    From: {complaint.employeeName} {complaint.zanId ? `(ZanID: ${complaint.zanId})` : ''}
                  </p>
                  <p className="text-sm text-muted-foreground">Submitted: {format(parseISO(complaint.submissionDate), 'PPP')}</p>
                  <p className="text-sm mt-1"><strong>Details Preview:</strong> {complaint.details.substring(0, 150)}{complaint.details.length > 150 ? "..." : ""}</p>
                  {complaint.rejectionReason && <p className="text-sm text-destructive"><span className="font-medium">Rejection Reason:</span> {complaint.rejectionReason}</p>}
                  
                  <div className="mt-3 pt-3 border-t flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2">
                    <Button size="sm" variant="outline" onClick={() => { setSelectedComplaint(complaint); setIsDetailsModalOpen(true); }}>
                        <Eye className="mr-2 h-4 w-4"/>Tazama Maelezo Kamili
                    </Button>
                    {complaint.reviewStage === 'initial' && (complaint.status === "Submitted" || complaint.status === "Under Review" || complaint.status === "Under Review - Additional Information Provided") && (
                      <>
                        <Button size="sm" onClick={() => handleInitialAction(complaint.id, 'forward')}>Thibitisha &amp; Peleka kwa Tume ya Ukaguzi</Button>
                        <Button size="sm" variant="destructive" onClick={() => handleInitialAction(complaint.id, 'reject_initial')}>Kataa Lalamiko</Button>
                        <Button size="sm" variant="secondary" onClick={() => openActionModal(complaint, "request_info")}>
                            <Info className="mr-2 h-4 w-4"/>Omba Maelezo Zaidi
                        </Button>
                         <Button size="sm" className="bg-green-600 hover:bg-green-700 text-white" onClick={() => openActionModal(complaint, "resolve")}>
                            <CheckCircle className="mr-2 h-4 w-4"/>Weka Kama Imetatuliwa (Moja kwa Moja)
                        </Button>
                      </>
                    )}
                    {complaint.reviewStage === 'commission_review' && complaint.status === "Awaiting Commission Review" && complaint.reviewedBy === role && (
                        <>
                            <Button size="sm" className="bg-green-600 hover:bg-green-700 text-white" onClick={() => handleCommissionDecision(complaint.id, 'commission_approve')}>Tatua (Tumekubali)</Button>
                            <Button size="sm" variant="destructive" onClick={() => handleCommissionDecision(complaint.id, 'commission_reject')}>Kataa (Tumekataa)</Button>
                        </>
                    )}
                  </div>
                </div>
              ))
            ) : (
              <p className="text-muted-foreground">Hakuna malalamiko yaliyokabidhiwa au yanayosubiri ukaguzi wako kwa sasa.</p>
            )}
          </CardContent>
        </Card>
      )}

      {selectedComplaint && isDetailsModalOpen && (
        <Dialog open={isDetailsModalOpen} onOpenChange={setIsDetailsModalOpen}>
          <DialogContent className="sm:max-w-lg">
            <DialogHeader>
              <DialogTitle>Maelezo ya Lalamiko: {selectedComplaint.id}</DialogTitle>
              <DialogDescription>
                Kutoka: <strong>{selectedComplaint.employeeName}</strong> ({selectedComplaint.zanId || 'Hakijulikani'}) | Aina: {selectedComplaint.complaintType}
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4 py-4 text-sm max-h-[70vh] overflow-y-auto">
              <div><strong className="text-muted-foreground">Kichwa:</strong> <p className="mt-1">{selectedComplaint.subject}</p></div>
              <div><strong className="text-muted-foreground">Maelezo Kamili:</strong> <p className="mt-1 whitespace-pre-wrap">{selectedComplaint.details}</p></div>
              <div><strong className="text-muted-foreground">Iliwasilishwa Mnamo:</strong> {format(parseISO(selectedComplaint.submissionDate), 'PPP p')}</div>
              <div><strong className="text-muted-foreground">Hali:</strong> <span className="text-primary">{selectedComplaint.status}</span></div>
              
              {(role === ROLES.DO || role === ROLES.HHRMD) && (
                <Card className="mt-3 bg-blue-50 border-blue-200">
                  <CardHeader className="pb-2 pt-3">
                    <CardTitle className="text-base text-blue-700">Maelezo ya Mawasiliano (Siri)</CardTitle>
                  </CardHeader>
                  <CardContent className="text-xs space-y-1 pb-3">
                    <p><strong>Simu ya Mlalamikaji:</strong> {selectedComplaint.complainantPhoneNumber || 'Haijatolewa'}</p>
                    <p><strong>Simu ya Mlezi:</strong> {selectedComplaint.nextOfKinPhoneNumber || 'Haijatolewa'}</p>
                  </CardContent>
                </Card>
              )}

              {selectedComplaint.zanId && (
                <Card className="mt-3 bg-secondary/20">
                    <CardHeader className="pb-2 pt-3">
                        <CardTitle className="text-base">Maelezo ya Mfanyakazi</CardTitle>
                    </CardHeader>
                    <CardContent className="text-xs space-y-1 pb-3">
                        <p><strong>Idara:</strong> {selectedComplaint.department || 'Hakijulikani'}</p>
                        <p><strong>Cheo/Nafasi:</strong> {selectedComplaint.cadre || 'Hakijulikani'}</p>
                    </CardContent>
                </Card>
              )}

               <div className="pt-3 mt-3 border-t">
                    <Label className="font-semibold">Nyaraka Zilizofungwa</Label>
                    <div className="mt-2 space-y-2">
                    {selectedComplaint.attachments && selectedComplaint.attachments.length > 0 ? (
                        selectedComplaint.attachments.map((doc, index) => (
                            <div key={index} className="flex items-center justify-between p-2 rounded-md border bg-secondary/50 text-sm">
                                <div className="flex items-center gap-2">
                                    <FileText className="h-4 w-4 text-muted-foreground" />
                                    <span className="font-medium text-foreground truncate" title={doc}>{doc}</span>
                                </div>
                                <Button asChild variant="link" size="sm" className="h-auto p-0 flex-shrink-0">
                                    <a href="#" onClick={(e) => e.preventDefault()} target="_blank" rel="noopener noreferrer">Tazama Hati</a>
                                </Button>
                            </div>
                        ))
                    ) : (
                        <p className="text-muted-foreground text-sm">Hakuna nyaraka zilizofungwa kwenye ombi hili.</p>
                    )}
                    </div>
                </div>

               {selectedComplaint.officerComments && (
                <div className="mt-2 pt-2 border-t">
                  <strong className="text-muted-foreground">Maoni/Mrejesho wa Afisa:</strong>
                  <p className="mt-1 whitespace-pre-wrap">{selectedComplaint.officerComments}</p>
                </div>
              )}
              {selectedComplaint.rejectionReason && (
                <div className="mt-2 pt-2 border-t">
                  <strong className="text-muted-foreground text-destructive">Sababu ya Kukataliwa:</strong>
                  <p className="mt-1 whitespace-pre-wrap text-destructive">{selectedComplaint.rejectionReason}</p>
                </div>
              )}
              {selectedComplaint.internalNotes && (
                <div className="mt-2 pt-2 border-t">
                  <strong className="text-muted-foreground">Maelezo ya Ndani:</strong>
                  <p className="mt-1 whitespace-pre-wrap bg-yellow-50 p-2 rounded border border-yellow-200">{selectedComplaint.internalNotes}</p>
                </div>
              )}
            </div>
            <DialogFooter>
              <DialogClose asChild>
                <Button type="button" variant="outline">Funga</Button>
              </DialogClose>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      )}

       {selectedComplaint && isActionModalOpen && (actionType === 'resolve' || actionType === 'request_info') && (
        <Dialog open={isActionModalOpen} onOpenChange={setIsActionModalOpen}>
            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>
                        {actionType === 'resolve' && "Tatua Lalamiko Moja kwa Moja"}
                        {actionType === 'request_info' && "Omba Maelezo Zaidi"}
                        : {selectedComplaint.id}
                    </DialogTitle>
                    <DialogDescription>
                        Kwa lalamiko la <strong>{selectedComplaint.employeeName}</strong>. 
                        {actionType === 'request_info' ? " Bainisha maelezo ya ziada yanayohitajika kutoka kwa mfanyakazi." : " Toa maoni yako/sababu ya hatua hii."}
                    </DialogDescription>
                </DialogHeader>
                <div className="py-4 space-y-4">
                    <div>
                        <Label htmlFor="officerActionCommentLegacy">
                            {actionType === 'request_info' ? "Maelezo/Ufafanuzi Unaohitajika kutoka kwa Mfanyakazi:" : "Maoni ya Afisa / Sababu ya Hatua:"}
                        </Label>
                        <Textarea
                            id="officerActionCommentLegacy"
                            placeholder="Ingiza maoni yako hapa..."
                            value={officerActionComment}
                            onChange={(e) => setOfficerActionComment(e.target.value)}
                            rows={5}
                            className="mt-1"
                        />
                    </div>
                     <div>
                        <Label htmlFor="officerInternalNoteLegacy">Maelezo ya Ndani (Si Lazima, kwa uwekaji kumbukumbu)</Label>
                        <Textarea
                            id="officerInternalNoteLegacy"
                            placeholder="Ongeza maelezo yoyote ya ndani hapa..."
                            value={officerInternalNote}
                            onChange={(e) => setOfficerInternalNote(e.target.value)}
                            rows={3}
                            className="mt-1"
                        />
                    </div>
                    <div>
                        <Label htmlFor="officerAttachmentLegacy">Funga Hati ya Majibu Rasmi (Si Lazima, PDF tu)</Label>
                        <Input id="officerAttachmentLegacy" type="file" accept=".pdf" className="mt-1"/>
                        <p className="text-xs text-muted-foreground mt-1">
                            Hii ni mchanganyiko. Utaratibu wa kupakia faili haujakamilishwa kwenye mchezo huu.
                        </p>
                    </div>
                </div>
                <DialogFooter>
                    <Button variant="outline" onClick={() => { setIsActionModalOpen(false); setSelectedComplaint(null); setActionType(null); }}>Ghairi</Button>
                    <Button 
                        onClick={handleOfficerSubmitLegacyAction} 
                        disabled={!officerActionComment.trim()}
                        className={
                            actionType === 'resolve' ? "bg-green-600 hover:bg-green-700 text-white" :
                            "" 
                        }
                    >
                        {actionType === 'resolve' && <><CheckCircle className="mr-2 h-4 w-4"/>Thibitisha Utatuzi</>}
                        {actionType === 'request_info' && <><MessageSquarePlus className="mr-2 h-4 w-4"/>Tuma Ombi la Maelezo</>}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
      )}

      {currentRequestToAction && isRejectionModalOpen && (
        <Dialog open={isRejectionModalOpen} onOpenChange={setIsRejectionModalOpen}>
            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Kataa Lalamiko: {currentRequestToAction.id}</DialogTitle>
                    <DialogDescription>
                        Tafadhali toa sababu ya kukataa lalamiko la <strong>{currentRequestToAction.employeeName}</strong>. Sababu hii itaonekana.
                    </DialogDescription>
                </DialogHeader>
                <div className="py-4">
                    <Textarea
                        placeholder="Ingiza sababu ya kukataa hapa..."
                        value={rejectionReasonInput}
                        onChange={(e) => setRejectionReasonInput(e.target.value)}
                        rows={4}
                    />
                </div>
                <DialogFooter>
                    <Button variant="outline" onClick={() => { setIsRejectionModalOpen(false); setCurrentRequestToAction(null); setRejectionReasonInput(''); }}>Ghairi</Button>
                    <Button variant="destructive" onClick={handleRejectionSubmit} disabled={!rejectionReasonInput.trim()}>Wasilisha Ukataliaji</Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
      )}

      {/* Employee Provide Additional Information Modal */}
      {selectedComplaintForInfo && isProvideInfoModalOpen && (
        <Dialog open={isProvideInfoModalOpen} onOpenChange={setIsProvideInfoModalOpen}>
          <DialogContent className="sm:max-w-lg">
            <DialogHeader>
              <DialogTitle>Toa Maelezo ya Ziada</DialogTitle>
              <DialogDescription>
                Kwa lalamiko: <strong>{selectedComplaintForInfo.subject}</strong>
              </DialogDescription>
            </DialogHeader>
            <div className="py-4 space-y-4">
              {selectedComplaintForInfo.officerComments && (
                <div className="p-3 bg-amber-50 border border-amber-200 rounded-md">
                  <Label className="text-sm font-medium text-amber-800">Ombi la Afisa:</Label>
                  <p className="text-sm text-amber-700 mt-1 whitespace-pre-wrap">{selectedComplaintForInfo.officerComments}</p>
                </div>
              )}
              <div>
                <Label htmlFor="additionalInfo">Maelezo Yako ya Ziada:</Label>
                <Textarea
                  id="additionalInfo"
                  placeholder="Tafadhali toa maelezo ya ziada yaliyoombwa na afisa mkaguzi..."
                  value={additionalInfo}
                  onChange={(e) => setAdditionalInfo(e.target.value)}
                  rows={6}
                  className="mt-1"
                />
              </div>
            </div>
            <DialogFooter>
              <Button variant="outline" onClick={() => {
                setIsProvideInfoModalOpen(false);
                setSelectedComplaintForInfo(null);
                setAdditionalInfo('');
              }}>Ghairi</Button>
              <Button 
                onClick={handleProvideAdditionalInfo} 
                disabled={!additionalInfo.trim()}
                className="bg-blue-600 hover:bg-blue-700 text-white"
              >
                <Send className="mr-2 h-4 w-4"/>
                Wasilisha Maelezo ya Ziada
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      )}
    </div>
  );
}
