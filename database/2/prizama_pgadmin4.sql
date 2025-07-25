PGDMP                      }            prizma    17.4    17.5 W    �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false            �           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false            �           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false            �           1262    16384    prizma    DATABASE     l   CREATE DATABASE prizma WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en-US';
    DROP DATABASE prizma;
                     postgres    false                        2615    17500    public    SCHEMA     2   -- *not* creating schema, since initdb creates it
 2   -- *not* dropping schema, since initdb creates it
                     postgres    false            �           0    0    SCHEMA public    COMMENT         COMMENT ON SCHEMA public IS '';
                        postgres    false    5            �           0    0    SCHEMA public    ACL     +   REVOKE USAGE ON SCHEMA public FROM PUBLIC;
                        postgres    false    5            �            1259    17501    CadreChangeRequest    TABLE     �  CREATE TABLE public."CadreChangeRequest" (
    id text NOT NULL,
    status text NOT NULL,
    "reviewStage" text NOT NULL,
    "newCadre" text NOT NULL,
    reason text,
    "studiedOutsideCountry" boolean,
    documents text[],
    "rejectionReason" text,
    "employeeId" text NOT NULL,
    "submittedById" text NOT NULL,
    "reviewedById" text,
    "createdAt" timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "updatedAt" timestamp(3) without time zone NOT NULL
);
 (   DROP TABLE public."CadreChangeRequest";
       public         heap r       postgres    false    5            �            1259    17507 	   Complaint    TABLE       CREATE TABLE public."Complaint" (
    id text NOT NULL,
    "complaintType" text NOT NULL,
    subject text NOT NULL,
    details text NOT NULL,
    "complainantPhoneNumber" text NOT NULL,
    "nextOfKinPhoneNumber" text NOT NULL,
    attachments text[],
    status text NOT NULL,
    "reviewStage" text NOT NULL,
    "officerComments" text,
    "internalNotes" text,
    "rejectionReason" text,
    "complainantId" text NOT NULL,
    "assignedOfficerRole" text NOT NULL,
    "reviewedById" text,
    "createdAt" timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "updatedAt" timestamp(3) without time zone NOT NULL
);
    DROP TABLE public."Complaint";
       public         heap r       postgres    false    5            �            1259    17513    ConfirmationRequest    TABLE     	  CREATE TABLE public."ConfirmationRequest" (
    id text NOT NULL,
    status text NOT NULL,
    "reviewStage" text NOT NULL,
    documents text[],
    "rejectionReason" text,
    "employeeId" text NOT NULL,
    "submittedById" text NOT NULL,
    "reviewedById" text,
    "decisionDate" timestamp(3) without time zone,
    "commissionDecisionDate" timestamp(3) without time zone,
    "createdAt" timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "updatedAt" timestamp(3) without time zone NOT NULL
);
 )   DROP TABLE public."ConfirmationRequest";
       public         heap r       postgres    false    5            �            1259    17519    Employee    TABLE     �  CREATE TABLE public."Employee" (
    id text NOT NULL,
    "employeeEntityId" text,
    name text NOT NULL,
    gender text NOT NULL,
    "profileImageUrl" text,
    "dateOfBirth" timestamp(3) without time zone,
    "placeOfBirth" text,
    region text,
    "countryOfBirth" text,
    "zanId" text NOT NULL,
    "phoneNumber" text,
    "contactAddress" text,
    "zssfNumber" text,
    "payrollNumber" text,
    cadre text,
    "salaryScale" text,
    ministry text,
    department text,
    "appointmentType" text,
    "contractType" text,
    "recentTitleDate" timestamp(3) without time zone,
    "currentReportingOffice" text,
    "currentWorkplace" text,
    "employmentDate" timestamp(3) without time zone,
    "confirmationDate" timestamp(3) without time zone,
    "retirementDate" timestamp(3) without time zone,
    status text,
    "ardhilHaliUrl" text,
    "confirmationLetterUrl" text,
    "jobContractUrl" text,
    "birthCertificateUrl" text,
    "institutionId" text NOT NULL
);
    DROP TABLE public."Employee";
       public         heap r       postgres    false    5            �            1259    17524    EmployeeCertificate    TABLE     �   CREATE TABLE public."EmployeeCertificate" (
    id text NOT NULL,
    type text NOT NULL,
    name text NOT NULL,
    url text,
    "employeeId" text NOT NULL
);
 )   DROP TABLE public."EmployeeCertificate";
       public         heap r       postgres    false    5            �            1259    17529    Institution    TABLE     T   CREATE TABLE public."Institution" (
    id text NOT NULL,
    name text NOT NULL
);
 !   DROP TABLE public."Institution";
       public         heap r       postgres    false    5            �            1259    17534    LwopRequest    TABLE     %  CREATE TABLE public."LwopRequest" (
    id text NOT NULL,
    status text NOT NULL,
    "reviewStage" text NOT NULL,
    duration text NOT NULL,
    reason text NOT NULL,
    documents text[],
    "rejectionReason" text,
    "employeeId" text NOT NULL,
    "submittedById" text NOT NULL,
    "reviewedById" text,
    "createdAt" timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "updatedAt" timestamp(3) without time zone NOT NULL,
    "endDate" timestamp(3) without time zone,
    "startDate" timestamp(3) without time zone
);
 !   DROP TABLE public."LwopRequest";
       public         heap r       postgres    false    5            �            1259    17540    Notification    TABLE       CREATE TABLE public."Notification" (
    id text NOT NULL,
    message text NOT NULL,
    link text,
    "isRead" boolean DEFAULT false NOT NULL,
    "userId" text NOT NULL,
    "createdAt" timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);
 "   DROP TABLE public."Notification";
       public         heap r       postgres    false    5            �            1259    17547    PromotionRequest    TABLE     &  CREATE TABLE public."PromotionRequest" (
    id text NOT NULL,
    status text NOT NULL,
    "reviewStage" text NOT NULL,
    "proposedCadre" text NOT NULL,
    "promotionType" text NOT NULL,
    "studiedOutsideCountry" boolean,
    documents text[],
    "rejectionReason" text,
    "employeeId" text NOT NULL,
    "submittedById" text NOT NULL,
    "reviewedById" text,
    "createdAt" timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "updatedAt" timestamp(3) without time zone NOT NULL,
    "commissionDecisionReason" text
);
 &   DROP TABLE public."PromotionRequest";
       public         heap r       postgres    false    5            �            1259    17553    ResignationRequest    TABLE     �  CREATE TABLE public."ResignationRequest" (
    id text NOT NULL,
    status text NOT NULL,
    "reviewStage" text NOT NULL,
    "effectiveDate" timestamp(3) without time zone NOT NULL,
    reason text,
    documents text[],
    "rejectionReason" text,
    "employeeId" text NOT NULL,
    "submittedById" text NOT NULL,
    "reviewedById" text,
    "createdAt" timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "updatedAt" timestamp(3) without time zone NOT NULL
);
 (   DROP TABLE public."ResignationRequest";
       public         heap r       postgres    false    5            �            1259    17559    RetirementRequest    TABLE     .  CREATE TABLE public."RetirementRequest" (
    id text NOT NULL,
    status text NOT NULL,
    "reviewStage" text NOT NULL,
    "retirementType" text NOT NULL,
    "illnessDescription" text,
    "proposedDate" timestamp(3) without time zone NOT NULL,
    "delayReason" text,
    documents text[],
    "rejectionReason" text,
    "employeeId" text NOT NULL,
    "submittedById" text NOT NULL,
    "reviewedById" text,
    "createdAt" timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "updatedAt" timestamp(3) without time zone NOT NULL
);
 '   DROP TABLE public."RetirementRequest";
       public         heap r       postgres    false    5            �            1259    17565    SeparationRequest    TABLE     �  CREATE TABLE public."SeparationRequest" (
    id text NOT NULL,
    type text NOT NULL,
    status text NOT NULL,
    "reviewStage" text NOT NULL,
    reason text NOT NULL,
    documents text[],
    "rejectionReason" text,
    "employeeId" text NOT NULL,
    "submittedById" text NOT NULL,
    "reviewedById" text,
    "createdAt" timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "updatedAt" timestamp(3) without time zone NOT NULL
);
 '   DROP TABLE public."SeparationRequest";
       public         heap r       postgres    false    5            �            1259    17571    ServiceExtensionRequest    TABLE     1  CREATE TABLE public."ServiceExtensionRequest" (
    id text NOT NULL,
    status text NOT NULL,
    "reviewStage" text NOT NULL,
    "currentRetirementDate" timestamp(3) without time zone NOT NULL,
    "requestedExtensionPeriod" text NOT NULL,
    justification text NOT NULL,
    documents text[],
    "rejectionReason" text,
    "employeeId" text NOT NULL,
    "submittedById" text NOT NULL,
    "reviewedById" text,
    "createdAt" timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "updatedAt" timestamp(3) without time zone NOT NULL
);
 -   DROP TABLE public."ServiceExtensionRequest";
       public         heap r       postgres    false    5            �            1259    17577    User    TABLE     �  CREATE TABLE public."User" (
    id text NOT NULL,
    name text NOT NULL,
    username text NOT NULL,
    password text NOT NULL,
    role text NOT NULL,
    active boolean DEFAULT true NOT NULL,
    "employeeId" text,
    "institutionId" text NOT NULL,
    "createdAt" timestamp(3) without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    "updatedAt" timestamp(3) without time zone NOT NULL
);
    DROP TABLE public."User";
       public         heap r       postgres    false    5            �            1259    17584    _prisma_migrations    TABLE     �  CREATE TABLE public._prisma_migrations (
    id character varying(36) NOT NULL,
    checksum character varying(64) NOT NULL,
    finished_at timestamp with time zone,
    migration_name character varying(255) NOT NULL,
    logs text,
    rolled_back_at timestamp with time zone,
    started_at timestamp with time zone DEFAULT now() NOT NULL,
    applied_steps_count integer DEFAULT 0 NOT NULL
);
 &   DROP TABLE public._prisma_migrations;
       public         heap r       postgres    false    5            �          0    17501    CadreChangeRequest 
   TABLE DATA           �   COPY public."CadreChangeRequest" (id, status, "reviewStage", "newCadre", reason, "studiedOutsideCountry", documents, "rejectionReason", "employeeId", "submittedById", "reviewedById", "createdAt", "updatedAt") FROM stdin;
    public               postgres    false    217   .�       �          0    17507 	   Complaint 
   TABLE DATA           +  COPY public."Complaint" (id, "complaintType", subject, details, "complainantPhoneNumber", "nextOfKinPhoneNumber", attachments, status, "reviewStage", "officerComments", "internalNotes", "rejectionReason", "complainantId", "assignedOfficerRole", "reviewedById", "createdAt", "updatedAt") FROM stdin;
    public               postgres    false    218   	�       �          0    17513    ConfirmationRequest 
   TABLE DATA           �   COPY public."ConfirmationRequest" (id, status, "reviewStage", documents, "rejectionReason", "employeeId", "submittedById", "reviewedById", "decisionDate", "commissionDecisionDate", "createdAt", "updatedAt") FROM stdin;
    public               postgres    false    219   �       �          0    17519    Employee 
   TABLE DATA             COPY public."Employee" (id, "employeeEntityId", name, gender, "profileImageUrl", "dateOfBirth", "placeOfBirth", region, "countryOfBirth", "zanId", "phoneNumber", "contactAddress", "zssfNumber", "payrollNumber", cadre, "salaryScale", ministry, department, "appointmentType", "contractType", "recentTitleDate", "currentReportingOffice", "currentWorkplace", "employmentDate", "confirmationDate", "retirementDate", status, "ardhilHaliUrl", "confirmationLetterUrl", "jobContractUrl", "birthCertificateUrl", "institutionId") FROM stdin;
    public               postgres    false    220    �       �          0    17524    EmployeeCertificate 
   TABLE DATA           R   COPY public."EmployeeCertificate" (id, type, name, url, "employeeId") FROM stdin;
    public               postgres    false    221   ��       �          0    17529    Institution 
   TABLE DATA           1   COPY public."Institution" (id, name) FROM stdin;
    public               postgres    false    222   �"      �          0    17534    LwopRequest 
   TABLE DATA           �   COPY public."LwopRequest" (id, status, "reviewStage", duration, reason, documents, "rejectionReason", "employeeId", "submittedById", "reviewedById", "createdAt", "updatedAt", "endDate", "startDate") FROM stdin;
    public               postgres    false    223   �'      �          0    17540    Notification 
   TABLE DATA           \   COPY public."Notification" (id, message, link, "isRead", "userId", "createdAt") FROM stdin;
    public               postgres    false    224   �+      �          0    17547    PromotionRequest 
   TABLE DATA             COPY public."PromotionRequest" (id, status, "reviewStage", "proposedCadre", "promotionType", "studiedOutsideCountry", documents, "rejectionReason", "employeeId", "submittedById", "reviewedById", "createdAt", "updatedAt", "commissionDecisionReason") FROM stdin;
    public               postgres    false    225   �:      �          0    17553    ResignationRequest 
   TABLE DATA           �   COPY public."ResignationRequest" (id, status, "reviewStage", "effectiveDate", reason, documents, "rejectionReason", "employeeId", "submittedById", "reviewedById", "createdAt", "updatedAt") FROM stdin;
    public               postgres    false    226   A      �          0    17559    RetirementRequest 
   TABLE DATA           �   COPY public."RetirementRequest" (id, status, "reviewStage", "retirementType", "illnessDescription", "proposedDate", "delayReason", documents, "rejectionReason", "employeeId", "submittedById", "reviewedById", "createdAt", "updatedAt") FROM stdin;
    public               postgres    false    227   dE      �          0    17565    SeparationRequest 
   TABLE DATA           �   COPY public."SeparationRequest" (id, type, status, "reviewStage", reason, documents, "rejectionReason", "employeeId", "submittedById", "reviewedById", "createdAt", "updatedAt") FROM stdin;
    public               postgres    false    228   �J      �          0    17571    ServiceExtensionRequest 
   TABLE DATA           �   COPY public."ServiceExtensionRequest" (id, status, "reviewStage", "currentRetirementDate", "requestedExtensionPeriod", justification, documents, "rejectionReason", "employeeId", "submittedById", "reviewedById", "createdAt", "updatedAt") FROM stdin;
    public               postgres    false    229   �N      �          0    17577    User 
   TABLE DATA           �   COPY public."User" (id, name, username, password, role, active, "employeeId", "institutionId", "createdAt", "updatedAt") FROM stdin;
    public               postgres    false    230   �Q      �          0    17584    _prisma_migrations 
   TABLE DATA           �   COPY public._prisma_migrations (id, checksum, finished_at, migration_name, logs, rolled_back_at, started_at, applied_steps_count) FROM stdin;
    public               postgres    false    231   _o      �           2606    17592 *   CadreChangeRequest CadreChangeRequest_pkey 
   CONSTRAINT     l   ALTER TABLE ONLY public."CadreChangeRequest"
    ADD CONSTRAINT "CadreChangeRequest_pkey" PRIMARY KEY (id);
 X   ALTER TABLE ONLY public."CadreChangeRequest" DROP CONSTRAINT "CadreChangeRequest_pkey";
       public                 postgres    false    217            �           2606    17594    Complaint Complaint_pkey 
   CONSTRAINT     Z   ALTER TABLE ONLY public."Complaint"
    ADD CONSTRAINT "Complaint_pkey" PRIMARY KEY (id);
 F   ALTER TABLE ONLY public."Complaint" DROP CONSTRAINT "Complaint_pkey";
       public                 postgres    false    218            �           2606    17596 ,   ConfirmationRequest ConfirmationRequest_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY public."ConfirmationRequest"
    ADD CONSTRAINT "ConfirmationRequest_pkey" PRIMARY KEY (id);
 Z   ALTER TABLE ONLY public."ConfirmationRequest" DROP CONSTRAINT "ConfirmationRequest_pkey";
       public                 postgres    false    219            �           2606    17598 ,   EmployeeCertificate EmployeeCertificate_pkey 
   CONSTRAINT     n   ALTER TABLE ONLY public."EmployeeCertificate"
    ADD CONSTRAINT "EmployeeCertificate_pkey" PRIMARY KEY (id);
 Z   ALTER TABLE ONLY public."EmployeeCertificate" DROP CONSTRAINT "EmployeeCertificate_pkey";
       public                 postgres    false    221            �           2606    17600    Employee Employee_pkey 
   CONSTRAINT     X   ALTER TABLE ONLY public."Employee"
    ADD CONSTRAINT "Employee_pkey" PRIMARY KEY (id);
 D   ALTER TABLE ONLY public."Employee" DROP CONSTRAINT "Employee_pkey";
       public                 postgres    false    220            �           2606    17602    Institution Institution_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public."Institution"
    ADD CONSTRAINT "Institution_pkey" PRIMARY KEY (id);
 J   ALTER TABLE ONLY public."Institution" DROP CONSTRAINT "Institution_pkey";
       public                 postgres    false    222            �           2606    17604    LwopRequest LwopRequest_pkey 
   CONSTRAINT     ^   ALTER TABLE ONLY public."LwopRequest"
    ADD CONSTRAINT "LwopRequest_pkey" PRIMARY KEY (id);
 J   ALTER TABLE ONLY public."LwopRequest" DROP CONSTRAINT "LwopRequest_pkey";
       public                 postgres    false    223            �           2606    17606    Notification Notification_pkey 
   CONSTRAINT     `   ALTER TABLE ONLY public."Notification"
    ADD CONSTRAINT "Notification_pkey" PRIMARY KEY (id);
 L   ALTER TABLE ONLY public."Notification" DROP CONSTRAINT "Notification_pkey";
       public                 postgres    false    224            �           2606    17608 &   PromotionRequest PromotionRequest_pkey 
   CONSTRAINT     h   ALTER TABLE ONLY public."PromotionRequest"
    ADD CONSTRAINT "PromotionRequest_pkey" PRIMARY KEY (id);
 T   ALTER TABLE ONLY public."PromotionRequest" DROP CONSTRAINT "PromotionRequest_pkey";
       public                 postgres    false    225            �           2606    17610 *   ResignationRequest ResignationRequest_pkey 
   CONSTRAINT     l   ALTER TABLE ONLY public."ResignationRequest"
    ADD CONSTRAINT "ResignationRequest_pkey" PRIMARY KEY (id);
 X   ALTER TABLE ONLY public."ResignationRequest" DROP CONSTRAINT "ResignationRequest_pkey";
       public                 postgres    false    226            �           2606    17612 (   RetirementRequest RetirementRequest_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY public."RetirementRequest"
    ADD CONSTRAINT "RetirementRequest_pkey" PRIMARY KEY (id);
 V   ALTER TABLE ONLY public."RetirementRequest" DROP CONSTRAINT "RetirementRequest_pkey";
       public                 postgres    false    227            �           2606    17614 (   SeparationRequest SeparationRequest_pkey 
   CONSTRAINT     j   ALTER TABLE ONLY public."SeparationRequest"
    ADD CONSTRAINT "SeparationRequest_pkey" PRIMARY KEY (id);
 V   ALTER TABLE ONLY public."SeparationRequest" DROP CONSTRAINT "SeparationRequest_pkey";
       public                 postgres    false    228            �           2606    17616 4   ServiceExtensionRequest ServiceExtensionRequest_pkey 
   CONSTRAINT     v   ALTER TABLE ONLY public."ServiceExtensionRequest"
    ADD CONSTRAINT "ServiceExtensionRequest_pkey" PRIMARY KEY (id);
 b   ALTER TABLE ONLY public."ServiceExtensionRequest" DROP CONSTRAINT "ServiceExtensionRequest_pkey";
       public                 postgres    false    229            �           2606    17618    User User_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public."User"
    ADD CONSTRAINT "User_pkey" PRIMARY KEY (id);
 <   ALTER TABLE ONLY public."User" DROP CONSTRAINT "User_pkey";
       public                 postgres    false    230            �           2606    17620 *   _prisma_migrations _prisma_migrations_pkey 
   CONSTRAINT     h   ALTER TABLE ONLY public._prisma_migrations
    ADD CONSTRAINT _prisma_migrations_pkey PRIMARY KEY (id);
 T   ALTER TABLE ONLY public._prisma_migrations DROP CONSTRAINT _prisma_migrations_pkey;
       public                 postgres    false    231            �           1259    17621    Employee_zanId_key    INDEX     U   CREATE UNIQUE INDEX "Employee_zanId_key" ON public."Employee" USING btree ("zanId");
 (   DROP INDEX public."Employee_zanId_key";
       public                 postgres    false    220            �           1259    17622    Institution_name_key    INDEX     W   CREATE UNIQUE INDEX "Institution_name_key" ON public."Institution" USING btree (name);
 *   DROP INDEX public."Institution_name_key";
       public                 postgres    false    222            �           1259    17623    User_employeeId_key    INDEX     W   CREATE UNIQUE INDEX "User_employeeId_key" ON public."User" USING btree ("employeeId");
 )   DROP INDEX public."User_employeeId_key";
       public                 postgres    false    230            �           1259    17624    User_username_key    INDEX     Q   CREATE UNIQUE INDEX "User_username_key" ON public."User" USING btree (username);
 '   DROP INDEX public."User_username_key";
       public                 postgres    false    230            �           2606    17625 5   CadreChangeRequest CadreChangeRequest_employeeId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."CadreChangeRequest"
    ADD CONSTRAINT "CadreChangeRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES public."Employee"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 c   ALTER TABLE ONLY public."CadreChangeRequest" DROP CONSTRAINT "CadreChangeRequest_employeeId_fkey";
       public               postgres    false    4819    217    220            �           2606    17630 7   CadreChangeRequest CadreChangeRequest_reviewedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."CadreChangeRequest"
    ADD CONSTRAINT "CadreChangeRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE SET NULL;
 e   ALTER TABLE ONLY public."CadreChangeRequest" DROP CONSTRAINT "CadreChangeRequest_reviewedById_fkey";
       public               postgres    false    230    4842    217            �           2606    17635 8   CadreChangeRequest CadreChangeRequest_submittedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."CadreChangeRequest"
    ADD CONSTRAINT "CadreChangeRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 f   ALTER TABLE ONLY public."CadreChangeRequest" DROP CONSTRAINT "CadreChangeRequest_submittedById_fkey";
       public               postgres    false    230    4842    217            �           2606    17640 &   Complaint Complaint_complainantId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."Complaint"
    ADD CONSTRAINT "Complaint_complainantId_fkey" FOREIGN KEY ("complainantId") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 T   ALTER TABLE ONLY public."Complaint" DROP CONSTRAINT "Complaint_complainantId_fkey";
       public               postgres    false    4842    218    230            �           2606    17645 %   Complaint Complaint_reviewedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."Complaint"
    ADD CONSTRAINT "Complaint_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE SET NULL;
 S   ALTER TABLE ONLY public."Complaint" DROP CONSTRAINT "Complaint_reviewedById_fkey";
       public               postgres    false    218    4842    230            �           2606    17650 7   ConfirmationRequest ConfirmationRequest_employeeId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."ConfirmationRequest"
    ADD CONSTRAINT "ConfirmationRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES public."Employee"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 e   ALTER TABLE ONLY public."ConfirmationRequest" DROP CONSTRAINT "ConfirmationRequest_employeeId_fkey";
       public               postgres    false    219    4819    220            �           2606    17655 9   ConfirmationRequest ConfirmationRequest_reviewedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."ConfirmationRequest"
    ADD CONSTRAINT "ConfirmationRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE SET NULL;
 g   ALTER TABLE ONLY public."ConfirmationRequest" DROP CONSTRAINT "ConfirmationRequest_reviewedById_fkey";
       public               postgres    false    4842    219    230            �           2606    17660 :   ConfirmationRequest ConfirmationRequest_submittedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."ConfirmationRequest"
    ADD CONSTRAINT "ConfirmationRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 h   ALTER TABLE ONLY public."ConfirmationRequest" DROP CONSTRAINT "ConfirmationRequest_submittedById_fkey";
       public               postgres    false    219    4842    230            �           2606    17665 7   EmployeeCertificate EmployeeCertificate_employeeId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."EmployeeCertificate"
    ADD CONSTRAINT "EmployeeCertificate_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES public."Employee"(id) ON UPDATE CASCADE ON DELETE CASCADE;
 e   ALTER TABLE ONLY public."EmployeeCertificate" DROP CONSTRAINT "EmployeeCertificate_employeeId_fkey";
       public               postgres    false    220    4819    221            �           2606    17670 $   Employee Employee_institutionId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."Employee"
    ADD CONSTRAINT "Employee_institutionId_fkey" FOREIGN KEY ("institutionId") REFERENCES public."Institution"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 R   ALTER TABLE ONLY public."Employee" DROP CONSTRAINT "Employee_institutionId_fkey";
       public               postgres    false    222    220    4825            �           2606    17675 '   LwopRequest LwopRequest_employeeId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."LwopRequest"
    ADD CONSTRAINT "LwopRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES public."Employee"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 U   ALTER TABLE ONLY public."LwopRequest" DROP CONSTRAINT "LwopRequest_employeeId_fkey";
       public               postgres    false    220    4819    223            �           2606    17680 )   LwopRequest LwopRequest_reviewedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."LwopRequest"
    ADD CONSTRAINT "LwopRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE SET NULL;
 W   ALTER TABLE ONLY public."LwopRequest" DROP CONSTRAINT "LwopRequest_reviewedById_fkey";
       public               postgres    false    223    4842    230            �           2606    17685 *   LwopRequest LwopRequest_submittedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."LwopRequest"
    ADD CONSTRAINT "LwopRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 X   ALTER TABLE ONLY public."LwopRequest" DROP CONSTRAINT "LwopRequest_submittedById_fkey";
       public               postgres    false    4842    230    223            �           2606    17690 %   Notification Notification_userId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."Notification"
    ADD CONSTRAINT "Notification_userId_fkey" FOREIGN KEY ("userId") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE CASCADE;
 S   ALTER TABLE ONLY public."Notification" DROP CONSTRAINT "Notification_userId_fkey";
       public               postgres    false    224    230    4842            �           2606    17695 1   PromotionRequest PromotionRequest_employeeId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."PromotionRequest"
    ADD CONSTRAINT "PromotionRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES public."Employee"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 _   ALTER TABLE ONLY public."PromotionRequest" DROP CONSTRAINT "PromotionRequest_employeeId_fkey";
       public               postgres    false    220    4819    225            �           2606    17700 3   PromotionRequest PromotionRequest_reviewedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."PromotionRequest"
    ADD CONSTRAINT "PromotionRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE SET NULL;
 a   ALTER TABLE ONLY public."PromotionRequest" DROP CONSTRAINT "PromotionRequest_reviewedById_fkey";
       public               postgres    false    225    230    4842            �           2606    17705 4   PromotionRequest PromotionRequest_submittedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."PromotionRequest"
    ADD CONSTRAINT "PromotionRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 b   ALTER TABLE ONLY public."PromotionRequest" DROP CONSTRAINT "PromotionRequest_submittedById_fkey";
       public               postgres    false    230    225    4842            �           2606    17710 5   ResignationRequest ResignationRequest_employeeId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."ResignationRequest"
    ADD CONSTRAINT "ResignationRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES public."Employee"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 c   ALTER TABLE ONLY public."ResignationRequest" DROP CONSTRAINT "ResignationRequest_employeeId_fkey";
       public               postgres    false    226    4819    220                        2606    17715 7   ResignationRequest ResignationRequest_reviewedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."ResignationRequest"
    ADD CONSTRAINT "ResignationRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE SET NULL;
 e   ALTER TABLE ONLY public."ResignationRequest" DROP CONSTRAINT "ResignationRequest_reviewedById_fkey";
       public               postgres    false    230    4842    226                       2606    17720 8   ResignationRequest ResignationRequest_submittedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."ResignationRequest"
    ADD CONSTRAINT "ResignationRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 f   ALTER TABLE ONLY public."ResignationRequest" DROP CONSTRAINT "ResignationRequest_submittedById_fkey";
       public               postgres    false    230    4842    226                       2606    17725 3   RetirementRequest RetirementRequest_employeeId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."RetirementRequest"
    ADD CONSTRAINT "RetirementRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES public."Employee"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 a   ALTER TABLE ONLY public."RetirementRequest" DROP CONSTRAINT "RetirementRequest_employeeId_fkey";
       public               postgres    false    4819    220    227                       2606    17730 5   RetirementRequest RetirementRequest_reviewedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."RetirementRequest"
    ADD CONSTRAINT "RetirementRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE SET NULL;
 c   ALTER TABLE ONLY public."RetirementRequest" DROP CONSTRAINT "RetirementRequest_reviewedById_fkey";
       public               postgres    false    230    227    4842                       2606    17735 6   RetirementRequest RetirementRequest_submittedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."RetirementRequest"
    ADD CONSTRAINT "RetirementRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 d   ALTER TABLE ONLY public."RetirementRequest" DROP CONSTRAINT "RetirementRequest_submittedById_fkey";
       public               postgres    false    227    230    4842                       2606    17740 3   SeparationRequest SeparationRequest_employeeId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."SeparationRequest"
    ADD CONSTRAINT "SeparationRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES public."Employee"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 a   ALTER TABLE ONLY public."SeparationRequest" DROP CONSTRAINT "SeparationRequest_employeeId_fkey";
       public               postgres    false    228    4819    220                       2606    17745 5   SeparationRequest SeparationRequest_reviewedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."SeparationRequest"
    ADD CONSTRAINT "SeparationRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE SET NULL;
 c   ALTER TABLE ONLY public."SeparationRequest" DROP CONSTRAINT "SeparationRequest_reviewedById_fkey";
       public               postgres    false    4842    230    228                       2606    17750 6   SeparationRequest SeparationRequest_submittedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."SeparationRequest"
    ADD CONSTRAINT "SeparationRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 d   ALTER TABLE ONLY public."SeparationRequest" DROP CONSTRAINT "SeparationRequest_submittedById_fkey";
       public               postgres    false    4842    228    230                       2606    17755 ?   ServiceExtensionRequest ServiceExtensionRequest_employeeId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."ServiceExtensionRequest"
    ADD CONSTRAINT "ServiceExtensionRequest_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES public."Employee"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 m   ALTER TABLE ONLY public."ServiceExtensionRequest" DROP CONSTRAINT "ServiceExtensionRequest_employeeId_fkey";
       public               postgres    false    220    229    4819            	           2606    17760 A   ServiceExtensionRequest ServiceExtensionRequest_reviewedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."ServiceExtensionRequest"
    ADD CONSTRAINT "ServiceExtensionRequest_reviewedById_fkey" FOREIGN KEY ("reviewedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE SET NULL;
 o   ALTER TABLE ONLY public."ServiceExtensionRequest" DROP CONSTRAINT "ServiceExtensionRequest_reviewedById_fkey";
       public               postgres    false    4842    230    229            
           2606    17765 B   ServiceExtensionRequest ServiceExtensionRequest_submittedById_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."ServiceExtensionRequest"
    ADD CONSTRAINT "ServiceExtensionRequest_submittedById_fkey" FOREIGN KEY ("submittedById") REFERENCES public."User"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 p   ALTER TABLE ONLY public."ServiceExtensionRequest" DROP CONSTRAINT "ServiceExtensionRequest_submittedById_fkey";
       public               postgres    false    230    229    4842                       2606    17770    User User_employeeId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."User"
    ADD CONSTRAINT "User_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES public."Employee"(id) ON UPDATE CASCADE ON DELETE SET NULL;
 G   ALTER TABLE ONLY public."User" DROP CONSTRAINT "User_employeeId_fkey";
       public               postgres    false    220    4819    230                       2606    17775    User User_institutionId_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public."User"
    ADD CONSTRAINT "User_institutionId_fkey" FOREIGN KEY ("institutionId") REFERENCES public."Institution"(id) ON UPDATE CASCADE ON DELETE RESTRICT;
 J   ALTER TABLE ONLY public."User" DROP CONSTRAINT "User_institutionId_fkey";
       public               postgres    false    4825    222    230            �   �  x����n�6Ư�� r;�_wF�ޚ8p��@@I�D["��a��ao�'١��qڤ��H�V���w>:q�`_0�`l&�}�[V�Z��c|���������7���f��~p{;�||0I�oTM_04IS����֪dB���:��o`�Fj��+U5��eUИ�H����iR�^�S��������U�^|�--x����}_nVV��W�h�1^0�[�X,l�2��`��0|��$��8���ս�I��g�dRMf���,��1�2��8��^L��FI�=מ�m-S�4P�J؂��|�����'��P*k��C����=M���[=��-�8�m��W`Et+%_�Z����=�>����n�G�=T
Ps5ˉ��hal�P��q�1��Ϙ�֌Ո&*b�A}[�=ϩ���^Q�M���'����{N1p�iN����:p��\_���Ɠ�'��u�ݜE�@R&��}�G�g�r�����(2�Qt��5����Ő���w,�$��0ZV�<����QpTא����Zx���,�d-�7n�H������Mق���8-�A����m����3�
���1oEm΁2Gs5�HQ����5S���L) *Sxl�(�W�V[��{���P�+���l����`�`�fhz���M�>W�Ly<��Z��Z˘����"�%����6ZRD�{N�r�����F��u]���l��|�l�8��C9w6Ơ�j��$���J�%�n#�e�Xrb�:���ȼ8�x�	��uy~b���D�u>�x����A��p�$t�iO�X���-
?MM>�/H�)/SA[�1e3�-���=4XR�}�	�kؠ�[�.g5�Ơd�,w ���)]��B]KYF`BPCs�����%jg<j5�mE"��~��8����.�@?�̣��V��>��h�3�6u~n4���UEG�^p���%(��������FP��Qp�d�	��9���i����Y+� y�%7��J��:0��������FjP=+��v;*�zT^�����l?41(�(�� V��A��¡ko{�v�P�V����xp̘qM��?�:Lȁ��������,}�&���b�(H^��^p^��B���n�cR�⣌$�؟Ρ�촭Ђ$q��HئI�m��9���bqvt[\���çw(�ی	��;����X	���V29�Z �_�bKk3��\��K����y\\�      �   �
  x��Y�n�8}V����� ��}�7��x��I�����(�Z����R5���R�m'N�L-E]�{ϽtT�F X>7kμ��ϋ��L��m�����Zh�����$��EI��H�R��\��	��F���N�������u��,ƅm!�"uB����ɇ�[��3��^��3�m޶=k�ɒv��jYG���H�X!-��ES/�ބF�k벗��'��x�IE}�H3�F����%mȪn�xY�7�BZF��Eʎc��֪K��BB�ށ-q+��t��}�̴l��up�:�e{ڏY�-����.bY]�zT�ށtƖ�ֈ�8y�h��k۳�ü��eן�wW����pu�]�^�>�V�x+��E�'	��4s;���F��p Z����H�l���a4��W1�k#Y-�7o��\�a-�r���t�i�wf��m�S�wf���&20�\��I�b���+$�<mۿH�,:����HB�1B���H�	���"K�u�����#
�T�v,<j�Q0IA�fk��{�,9�Yɗ�>E�E]�h K^��� O#�WG��t�p�`چ��~�������7X�û�ﮯ��xǱ��d�q
�ɿ��j���~=<�vA[ ڶ<�JtN��4ktr���tg�ż�$=���o�C7䍉����+�*�hb�{fY�����9j߱�c�L�:~V�����?̿:^[����� 4���׏����:< �c�µi��]`�\;���.|��4���ih�+\�ֶ�x�1�k3t����V���d�w�VN4�}���@>-�4�O��
�c���a������?j߹A7(���|Q�F�EY˟��������Q�9S��>*������޽�npy���v������ϋ�#nyO�Xoǭ��[Y�v�Q�����Z��7�WƔ=�Nh��������ՎSoon�߿�~{�$�~���E�Q�
��X )�&����ּ[�u��5�=��'�-G2H�Zp��9]Ў႓i�#�Z��5��/j<h� \q����XB�@IA1Z��1��+c߁t%��:�eoA�jNq�b[1"rN	�@r"���κ^-����A�!�k�����������Q���ti84+��s���۬.y�&P����gں�O�+4�NňfڭA���l�>�X�0�o0�6��E�j<��}�%��S-V'�)�-�Z+�4�@CN�3'p�����,�?�
=䌜�(W"�{��!���Np���}|�o$�X����n�C�W��.���Nɤ�2����[7|�=�9��Zވ��|�Lډ60�ΏP�2>���G촛c�� L���ؖ�a�l��� ���r�b�H8��@ �˟���n��y��6��.�����G�Q���8��E��k���0$i�\�z`���Gт��uԍ���3��]�WǞ���xj����&)��y�Z�k@�࿂ #�T���d��!n!��ȽT@r�*��J�v�������Ove��ݛ�����8w�ve�'��ͪ��vs߬7�:ZFD[�~ۙ�=b�=E�# Υ�-�8 ����}J��� P��g��#�%C�:�fh�s�"����xF��x!c4�&�H������O�g���/C���\��d��ђ"�a,Er�1����G�'F83��a�y�l�3��-�Q�I�¶0�-ձ�E�k�--iE�͠d��v.Ì&%�P��m�D6R�:�`6��=u�F��_[�����
�T�R�ж'�I�e��ءS7�0>�
A/$�I��Ii�̗9sB���3������EP�t�6u�ܗX��`=�m��더,d���T�Z��h�Qzզ��%1I�4�DF�d���L��廿a{A;;ХE��]�<������PA��mPFy�V��Ь�6N�4��8!8fI�� ᪒�I��$��< �	�'��C�A�Z^r*TV7e��j�!P��i�x+eYo�U�X��g��K�gV�Ǟ<��n��ClU�~����������ɜ�ص���Y1��V�}�CÒ^���c�1'-mG���"�.u]��Y��O���k�3�ދ\���q7F�/�e��G�u,�r����ͣ�+,�hz̩�Vm�@tRpġ/�8����V�����N4S$<�����UO�y����G2��=z���5s�4&�c]��K���z(��t��}-��w3G���㝖$-<!.ͳ>���tC��:D�.�24���YQ�2)�o���A�y�L%�P�H���~�ox3���6�j@9S����.��������>4�X|�j0���Q��O�{t|�~R�TYx����j)���Jb��Q֮�띢�)�T9����1��D���)��8	��w�]��uj�M�#G�W ��0CH"K6N}�d�r�Ə*\'��d@�I&��~qL�=5QM_��7G�W+Z���¹m#��W�Fm�|��d�Jn9?�d�Q�������m��J�̆lDX������B�4��M�%cxeQ��4���"Q��!S���xZ�+����}�;��,����[?���H��OO4�oA@�E]0J&8���ܓ`Q�i��do�I�?ѬQ�����e�L[J�c�2.1�u�ƽk����S+�[6�j�۬&��\^�9Q
&��CW�����W��N ��~��嫗�p.,����]�*9�C�۩��",��	yz^�nr���>���4��8�.��0~ގ,�����ŋ��g�      �   �  x��W�n�F]�_Ax��$��	�ҨHlCi�M ���D�R|IrP���?�t��)�Vl7q�t'�{%�{�C~ &x�  �����3�3������o����f6�4���}Y�eV8ggY��|�Ɓ�g~*�(O�2J�(�e�s#�7G�3���L>�<K�R���MEQ�a�G\�z��U"?4?�gyZG4�dL���Bx��knZዚ�r�E
�T$]���X�!���#d�9�:�6 ��ϗw!:��jqk`?�[��T>+���۝��6���]}�z�_}�0��qzu�
�I
O�ґ�F�� ����d��W��މ�ɇS%JY۫ĩ���Ť���t&u�vʲ�oV*Y6���jnu����&�D�m�2���zڐ��i��g)|�V�-�}#��^��ߴ>������o�+�nA��ѧs8T ����C]�މ�o+�R�|N�v=���^������L댎'�71�b��~�S<�z�SXl~bs�'���Qeq3`f� ���^z� ;��P�^��N45�%�fn羵������bY�H#��:>�)��%���CǶ����Z����^��W�ݯ�W�k�G�"�Ɂ�8;�ˁC�ჁCہS��2jϝ���U��\g��<н�~�&ITR�O�L
!K��tR�q���$~���}#������+^��^��s�����[��{A����qC{N`���~�oo��J�'4ڷ�]>�x{��;�5[�	т����p����CCv$�2 F����z�r��|����3��^FM��zU��4���Fһ�~���EP��Cm�"x�C �é���XZχM��,����x����.��2�������L2�p 6�m�CXt�d�A�\���]޶'� �[����n��V1��JC�<���!1̃?8���Q
��n{�
�Da�[=C�$tE�D� �ݤ-ˑ�fㇸl H��!mb���fs�y�ƛv>𢪷8���vWOYW�G��?���7nTFb~ �~���^�;�&�u�7�t�������^��d�T�-v8FQs��`����7���
Ǩ���l�b�x��x!(��ac5 11�3̠�A�ϧ��@;�=�����b����@9���Õ���=�%n|�B��#� u�mdX���ґ�6�_�
��,�ԟ��V��t�-��D�i]Nd8�q�_�h���p�]*���|� HmG��l�!ڔ�X� �ڥ&=�O-x��899��˨Y      �      x��}kS�:��gί�g��u󅪩)HhHCI��T�e�@'�M����YK�ݒe�r�=S{�8��Զ�xI�Y����_Q��J8���l��_|o�Z̋�c1�O����X�/��d�q�~Y���<�����n�v�_DD�������Y����{2��#Y�%��(QrE����������6��|6��?l�{W��\-�Gg�M�%I�FID�O���w�_#&X�f�y�^��foF_���n��t���S�����g�b4���y�g����t$������t<�']�����㣓|t��&�O�����w���r���\>s����������s�G�(~���>��lYޮ���~���������~v���%�5�L��b~?[>�w�S^,�gOo���[��rw���V�R�g�yO����]w���-׏oo�e����.���dsN�8y��by�ƿ�Д!������٬V��Ѥ�^�4N�(� E���&"�&�D#9-83_�u�wV��E���D�ei�0�e�[B�Dp���x�{*�d
;��Uy�8��O�%.%v���;���ʉ�F��^-�����wZ.B�I${�� ۇ2��2L�ZǠ��kܙ���-�] ��[ܼ�����@�Ѵ�� Gr��}�~\�r �bS��{_�ټ�M��3<�(�d_'9`0�SMZ��Y�f��kQ�E$2�����h
��]-^�,��,�+Y�}���[1z*F㱆%�i���l�^��A�J�CۅZ��kn'���kԙ�؋a��N������_������cq7��q^�V����)���Q��"�`��~�[<�M�!�&��c���̶�RQ����)ʸ-��B'�R�toZ�g�'��?h)' N�R�n�A
!�v�����WnW�F�kܙ����^\ �z[[R����6�m�\c�\c{�bv7:�<���XU�"mz��K`�ӷ4�~���ĚH܎k)I"F�`��H	GyL�4f��=H�9�|�_ Ϊ��-!X�X�4��6�hN���5K�1g���­��YG^�_���	,��?�2	��y�}��!���>#���p���ns��E�*IS��jߟf�4g~T	�*Q	���~-�Пecׯ��W5�uo��,�8 ��I֎���9}��9�˙�����rq��e/|�0T7xDxDx<�������lR0��O�g!�[	���D�)�����l����B��>�l~;{�;��e�.`��IA0}�/sD�����Zb&}������x���iv��i�{O��21���P����̽Ɯ	 ��@���N������$h�KMt�{G��Q�v1:�5�/\����)n�iS?|,��#�g���|'c�Gq�l1�E�ǘ�v��}3T-A([���=�ۈ�"F�R�����=�7\�5��f8��@�u�S��hc`/�^fb/�;)��=� �Nrii�Z8�X�Y�4F��7�i�%�M�k"e�6Mf�Q��ͭ�v��7����]�`����qm�40��kԽƝ���,s���:���B0G"sp6y��}ތ�k\MqI�¾?E�r�Hyۥ���Ma�
����`�Sc;�i�f�6uM��mK���s����f���n�6�>�I�˷��s�)�5�qg� �D��i�0��� �nև2j���h�r1N��L�Kb��I�E��Y#Q �`e�&�`���0���^�Z�V���="��m�R��`��'�P)$�z�3�C�5zK��Y�9:|4|�5�:��A�h8�=]%���N�,�G��5��'":T�6sS(�Ȍ,�ӌ�0�o�%���c�ΐ��'G4<�G�ZR`�� �fi�`�����3�3UC�Jd�4��
�2��el�DBt�q�{V&]��]K�{7i(��g9��bXP�j�˔=��I��\e���ܰ�8foF���L4�x�8+�G��αL%.��fAV(ܶú{�Pg��J��V���5������6{j��Y7	��E���Q�>��gϊ�*�a6=E5MpM��a[6 JŠ%��԰�s.�(���[+��ק��-�|%�s�#X��5Bc�tz*{���Cs&i�:h0W�5��H5"�pe�0��|�\���'C��Ä���D��	��K�a��3$2(��rة���̠[���Z�g��:�P���Y� K�>ɣ8�4��Y�mh�p�����1{j���1I�3����O���-�G�����6�ʀt70�)5<�~?� J�Zz%�Bɢ�{��.1�����?M9�Zx�Z�:��:�Ƥ��RLZdm3MCPJ��4Ԩ
#mI�*�����ź6��I��Lz��osww�[���Y1���0�L$�Kli��z�m3�}�r���cA�x�ǜA\:��,�Fܙ�aV#����H�J� �X�*�M�}s���:�'�t$�k�|l׸.�U��k���4�9���#җZ4�
���3��
&^c%�\]O�)����d<�_���������v�f�ns��n��n5hW1�fp�j�
�Q{jx��dˤ�xEe|�������'/JLx�Y�b�L�h��jw��-3fU���O"���b$�����΅�]b������E]�
4eK_.��X�)���׼Ɲ��4J���P!����4ݬi�
g��E�.���9��"��-T��a"�v*�^0a��N�0�U8~, `�z����l�ux��,�	2{��:��kJ��,a�L���r�=5��8SJ� fq�Tq��\��yt*�q$���j/�o�@�m�R��� �q�]"�fKi([z6�8�\��'_����t<B�>�r�y���݂����ʠ�]��p�Qg�`-�A�i��ƠR�A�U2P{,��w?f��S�5�҂��庄�,J)�bm �>K�s�>xc���������j�5'y��-8*�~��W ���${�p�����PtzJx:ר3�C�ʂ�4�Pc����ł�Y�IٷCH����ep�6qi�80F��>�<J��M�f���?H��z�t�Kv@��u9Gm��'��]ڣx�㨞���o^����b(S��Pc1�)�q-�Ύ��yY.��|�D<��(�%�i�0,�>�d�Ej�_�,%rT�d4�������+���^~~��><�>��:����j����}:,���^��1Dâ�t�k��3�C�J��4��#�hb��h���lӲ�5�,2�j�)G���Ǌ��`�{����.�4����k�(%��.��ѿ
k:�j���e���墸늩�wo2ɞ�5�顶v�k̙�!�
uq�i��F��4^g���q��9�:q���@�$�q�b*bA����*��G�A$J��f�h��X?H���w����9u)��5ێ֝�tX�S��ZfN�L���r=5��9�� �Y��p6�,��1�wu�pG�]2UfM�������mUxK�εA���ÑiG|
��aZ��=XS=�t �oӦ�:��F��笱QsflH$S�scOC���Ȣ �Y�/s�ߡ�.�D��bΖ.�+�ឪ�&i�2BR2���LS�x۫]���ԃ|�N�6�������l���u�:�K7�hhNW ]�^c�4 ](��P�.�f�*�O�r��������t��Y�t=�ޟ��	c-K������*�kY�
c�1ӎu�i��}�-!*}�;*�O��gD[WJⱱ��Ӗ_����ĽƝ��Pv���_;�h���`�8��m�^�_���(jy�'�;V���eC%�&Q�7���0��F|�;)��u��aHE�x�к�cGmtR\H��r�L�pG��� pY�p�U?���!���n	�y�a��Hc����H�1i�� ̴-���	�m���I~�qv�V�/���…*�n`������Ɯ��PWڞ�ha���[@{���b#�RͲ;�Y4mON��P��ƞ#P������3ceYg|
�f8: hYE;7ݼ�<yc�$a=�� b�騤X�s�o�    B�=5�¬Lβ.��ί4�J�P��+��]o[��.�m��~j�#�Q#%M��kVeڰ�j���c�,�����W��2��C��.����kܙ�!�
��4��
��8[ig�w��Ȩ74h}2��(NnE^K2�T�r&<�f24κtP�ܲ����mdR(W��瀯K�Q#?>?:�x,7i��_��|O��(� �@����{�r�Ӹ�N"�w�wC9ޞ��a/�9���}G4�ڼ.~���h4Z/�]-��:+����t2!�1=�Z����/�hdXR�o�r���杷ȷ^��zY,׽n��1��
� m!�fg�K�	�˲07]F۲,;ܢm�A�j>m�*	oGE�5���m]dZ��A�X:AS��Ť@\�x�Լ<:;��gy�u<�����/�>bz��HNڵv��]�gs�D�8ל��34���i�������fq�6�??��Y/W��������,�h�]L�����X4�)��
�����	3����R��qyÅw�?�в��O���,Ãöq\3i�L�5�#�V���c(���P�1���$��g�ӭVPI�5,|���j*$����'D��W�.����'����k���-��)hf\���0:9�6��j��CT��ˑd�:��P����Fa��i
-��N��n���f��&���N�+�*k�J�����L�f-u��&{y�+�I8È���<�0(����������~�_ME'�P��-�4D��]�+7�qgZ��0��i�A�s�A�	@�@���rq�c�Ah'��Um�n�R�UbMc���G���:�Ģi��q�V�5#��o����c�z@�9D��ě�����bMku�QgB��/8�|P>����<~OgS��V�Z<�$1��D���- �L���k��8�JK���5G�<D�Z�@5JG,�&��]�X��C�{�9s7k�qOC��0��� �Y1�M
X��ѻ��i�Iч{����$�h=\��pO��K]�����5K��0G�Ó��x�%���x-�e]��<�{��)G�0݋g���6�L-�8s�ܜ�R�pcV���Pָ���[k�msL��D�m�Ӈ���4�,s8ۦ;���(?U�W";Y��.:���bX8��ae1�r��ڵ��ʸ�z,��r��=��ٶSW�e_b�mﶴˍ��{�;�6a���=5��y�0�.g�DY������N0ij\!�J�<��	F5?ʘ��k�D��9��P��y_xt��dG[�3�C0j}�i�1f}�i�,��M2A���;�M�y]~���.��[b8O�ѫm�gwS��H�1O���������#]r�O��7����8�%�z|����_]\]TSѭ��ͽ�-���C��W&s�qg&� /�弧�F^��[���#7?����'��)F�E"��!���\�H6ӄ�w��tY3\7�2��v�G�8�8����C	a^+��r�3��Cq��R>��.̚���v��|�̵�qg; a�0F�)���|s7�9�P�K������Éf�o��	R���K	T1��Nq�?"_h"�}�:oM](cr���(�3X>a=?���Z�PCt�3M�����c�6�ĽƝY �D(���P�,���KK��0�������f�gUP�~QR+�jʸ��(�i��K�YH�F��,c&4��<�[��e�z�x]x���os7�)oP�Q�^�?#B�ݞ�ya��`Aȳ�]8_U����k��<Q�"��75��z)�#*�W�^�Y���r	M�Z����Q�����
}D.F�Y~ȿ�����U�Y?�^�eB�~�9�n����5w��*���i�AF�
����X�I� O���&}Р�KL����v��[���A������#�2��������\�M��є��&m݇��u��S-�����ؙtG:�����(g��~n#n慴�˪��4�s'l �B�ڞ�b�u�,��>cQ�1{Ye�l��Za��pv�)7ȳ�Fu2Sb'����ֻ��� E��3�&�ɻ�~�t�Q��,T8Բ,V�v&�x��V���b4Y�fO��+>��TmG/���{	���0�6�ku��\<���kt?�{2�ic:��/T��i���l
K�$�T�����/�οi�|l9��Y~{���e]�ʝ|�T]�t�O�5oROm7���Gfm�$��G����:�y�0��� �v��Dc�1Tm�{�j�@�r�6��db8ܯr@���a��W'��M[�C��z�b�a�rq�=�耳JMؚ�G��U���"��@��x��Cj?Mv&���*�!�b�v�q<_���b��[��}|OV.�YCC4���U����A���'ɞ*��������4Ԉ�Ab�8���I�^VFQ4[��<9ҕ�ys��SQEB�*n�� 3;�@���f����b͂��*�Q?�69M�x*c�g/���v�)��;xi]�5�7<kۻM	��s�[XL?�d�jԙ�!�@zj��Q �M�U(���KZ7�3�!fQ pV*`����^��<�]�u��	�Y�S<��0MV,�~o�fB�x���Rv*$��z�fv����|�<�/[r�-�hh��hr��>����1�C0J��4�#Cb�A��3#Fp�T�W���(mS*�3a���Ȑ�fIb�1&xk>�����l��S�]��5z�o��uXT |I�am��]�Gv�m�� �a&�8���y�,��N7w��?/�w�˪I5�s�?����$��/��dКŗ�]U�i��&����Ni$Ƅ�~�sm/�{g9f�>>[y����'��H��
E�p�,���n������E5��H| ��؏�T���]�ơ�GOC�0�#����d�7�o��W�Af����eU��du�!Ek�ەҘ��2TIʱ�^U��E&�&�|nj�v R�o��:�Ȼ���^e����Iz��U�#&g~@���#ɶ�㎐u&�i�!�L'A�����l|5�gu�3Ќ�T�w9��IS���-�����(1Ks��v9��ڥ(�2T�u;Otj: 
K[����o�S�5���,P�b-{K�Q�x��GB؞,
ߘ�!�u*�i���T[NE>�Bl��pv��]���%\�]l3題���d�a۳8%pk�1���4Ɠ��|�&{cL^�� �O�k4s�tx���C��DN=�2pG�F�S�����](���P�.�即�%˛�jxS-TU.��_*yט�@�h��p[��n��,�����[��x�8]ܯ_�e�}��3Q/�bOU,�G�9u�)���a:@�� IC�DA��Z8;*��M�*U���4��N�/K-� h���m��&C�E�h��y��bg�,2��z�k�1�,nv_űIC�xr��~�
ͧ5�3\t%��mOC�0�6!A�([8�39��6��j(mkIy$�n�Z*	�&�!2�<bKӵ	����4�=�si��l;�)�N��2����_�Q�T��q{�qbuM��<o�P��Zw����#`-A���x�LgI,���},^�J5�S���ǏZ��V����q��*���QӅ�a��V�i�A���9[-�i�{1���a�.:@�ή���I�y`b�Hn���#-�$Y�k2�$���i����&,]+g��r�xX/�U:���y�Ճ�}�t���Ő� �j�4�#3,��b��V\i*���n>C�vv�kR���s�G*��g�U�uk��fh�L7IS��U���(�u��d�j~I��ZOC��0�6�Aز�Z8C7"�W�;��?�|�e�St�Jw��Ɋ�Ͱҋm���M����E1]�Sː�X�f7����f����\ξ������Y.璆��t�N��8���nղF�i�D-�"Bf-e�UcΜa(���P�0��Ml2��0E�xy�Ye7?5��c8{��u�	�Ň-���ݮu$pO�QL2jU�κ�':�81C������H �����],��7>5�h��:}UM�ʞЃ(��>g(�3�C����P�/,9���g�l�u�?�xz*�'<z��)    #kz{kN&Y�%t+��S��d���1�s���^�9���:u��}٪Q���ޕ~ �h�pz��~�%ډ2�խ(	5)�4��3)$I�,�BRQ���r-�Qw�;<���� ᗠ?�vg<3�V�$"�k��*��Rk���}	��L���8��q��ӟd�k�2��� qfrC-=5�,�mY�����g�QF?����]�>�:�OXs����^�(���z!#Ͳ�#�j����,���g�o@�Bݷ�߈��:�M�Q����*Dԙ�a\o���a\o!��z�Le;���NeՎ�t�nm�{�y��S'��Lp�Y-�R����*�W=@u�W!{�9��,�:~S�Y��
_�ώ�����o�b�1�PF{���c�B��פ����dz%�1�:s��,��r�=5�¸���v �Br�?n��<�?r�g�9��Ҵ�Ҡ�(�\�\���V
{�l���ᖶ&�(3�ֈ$>�T��pԴ�� M�`c��}V���Pi�@��@r(h�JAU:��@�}����r���Ac(���P�1��Mi-�Φk���zv�M338���BG��~xx L��$��_SI�l=ވ𹇧� N�\���T+)ru�E�vZ|b1n��l��8���k�\i�Q���`���<�gvC�➆�a\qʂ�hq�pV���"�X�A>�ǺF��ޔ�"#pG��.�1`�q���i�85�U�L^����Yu�����?��Yʱ9�+�+I�@a��/h�W�9�;�.�E�i�Q�"�6��9�ndլ���7?�F��"�٤X~/���_D<O&��
:��;y*�V�8J�xg�Q+�M�wE�$�� j�.zu��{�WwWw�-���U�Ƌ�@����ڳ$�y��{j؅�Ʃ����N���fvW���� q:�2wW��/��>��|� �괕"ff�h��AI5�ny
��S��PmJ��`���Y*�ę��Jl�ĆQ�i%��3� 0ٺ�@��ղiׅ՞������-U�Y,���u�O5��i)��_���VQq�����r||%A���|o۰G����ʧ�:��{���Ʀr٤��%�ɂ�J��Iv5����oOC�0�7��Sb�'�����Qܔt�g�����
�;�=:<��V�R�B�+�,���G�L�����Sxi������Vg�|�q��M�M����2L�ٕ��ފ)���l�*��;��J��4���~�4~�S9�M�Ϟ�V��(C4�#�D�x�ݫ;�8��ʟ<�RbF5����0��ک<�U��`�flj�w�ѫ��(z�Q��1�FO�B��N�@�A�h?K�����A\�;yOC��0w�4A\f���ns��r+���.����N�/qӦ2aW[�^m�Z�v�bn�]I�3mv�� u�ax8�8�\�_��Z= .랞��Dn�ԈtP���]��耋}&C��3�p��&�i�p����af�,^�w���"��a�����7���v�y6���`-�0h)� c"ά2b���4���;��/Ӗ���q��ۉFw2��a�g�D�Pn��d��i�bp!O��J26�u "C-=5"�,	A$�	g���qv�b����C���D�N%#�I���2��� �N�Q��3��h�q+�m�����'kn��>�*S��L}yUr5�Y�U^J_T�3�[��Bn�Y����L�$A�@�|�,U�}��ݧ���\�4�
g�������98hC���IyO��?w&�Mྶ�����J|f�L�!����]�����������7$��lU��2x�K�"/<�\�8Z��/�g��\�����gud��ZEj��i�%Y�"�-X��DIV>����+�C�g����Q�Z���T)�%b��e��UkS�J�Y�"
�VgI��?��˧3�ֈ�a�+ew�~7$��n�����5�h7�/\���h2"� J1�MTǯ&;o�B=5��e����P����b������~�y�E�T6|m�M�U�$L�'T �Fm��0!k�#����Xt'�#���Ѹe7�\K��L��6a�mf{�D�,����Z/7�HA����Ƈrŕ�m���l�nYmz`�=Df*�4�.����|�~�����]}<�b�'�����Csp�c�͈dD|����4�3\cU�z����������9"�B��{j(��.2���&��P������E�Y�8��M!�>�C^w���{�J���=-��YL��2��]$r��x�Id�ZE��qۍ���J/V��3D�0��p+�}�ju����H�)̙�!��b�4���bdI�,+�Mg���ZW��JT�TL���N}~xiү�]��L��8!]ԉ6]�ђ<���j�~��G�|RI��kh����I��J�0�?ʐF&��ə�!�5\�4Ԉ3\di�,��M�}�ݖ3�@d��v�Űb�G:&�.Uv�0!��D��R
��Y���,�\M2m��T�s��ns���Yԑ:��Ѿ�z����4����7V}�Yb�v ��k̙�!X5i�4�X3idv�L�M��h��?��I,��2i������y�=ot�΃>�N4�Q4M�}h��S�kϧ�(0cf�����}�n�.+h��� �m��m���� �A\��(=�y�939 0+� �k����V&�EA��c8�1c�g���]��''5�_OTk0I�4�L���h�a>��7"� �w3�Mc@ҦΒ��#������w���H(G��rD�pEm\Ѫ�(�`W��9o=�w��&��������#<#���禛{goF���ͬoT�~����6zpEUsB|�t�VO�
w�����W�.7~����a�;�9HօFZ���0kQ��.��L��IӴ��a����Ŷt����a��U��7��*<fD���;Ѱ� %"ٞ�}5̷��������4��rr���m�L�,$��?��Ӡ���2�HGd�#2�b�@�w��� `�=�ZV�3{T�409*%?���%+�s��)l`
�5{�l�ZF�V;�"�(���2ݙ�e ��f,���dx�%+�F������&�j%���z��.��'_$�|}r}z=�U�,�u�f���b�ܼt�)S:�o�n����6�ʸ %N��0�������h��|_�
�a�uծ���d�F��r~�zYj��6D���e9_�nG-՛庨���.�g��r�������`��?��$��R�SO�޿���FY�;&��K�_G���:b� N������uu�'0�O+�x��+0?���;[Y���@�ؠL��� u=�P_�?i2�=��;i�������XY��`����0m�O��'���&��UO��	{}y��p�����w4C]������s�t�֑�A�r��k� ������f(��ײ�f�]�����d٧����<���Lml�2���k��y��,�88��TW[l���X�i`rF���ֵ���i����Tk?��\l�20��,�f󇮒P��v������ުZ�4�E�@���G��wGw�xB	ﾖ�(�]3��N�Yg���R�����R���Z��sU�  +��$3��R"�ߑA��(�+C����7�rc��h�S�Z��X�1{s�3�]��\c0���lea0� ��n8�a�SY��+�{���X�NtI�4OB*͛ET��4�;��sH �<O���%?�_�%��<����l�M�'�G�
��#8�u��ʣ�0yPD(��b?����!�#�wO�ʷ5��&��Kt�e���!�!Z�n�G����e�u#��q����L���*oi��.߹&S�7��qneJ#�v%�'�o����dD>,���ޒ�AL�!�b��b X��e�9s'rC]��ZVhu�&Ah��q8��M��[=�)���b��1ܭ;�b����s)3�k���+�Q��F=Q3ei����}G΃LtY�C��v�*�8�1Y�ˑ�N��;��$#/���je!�!Ϧ���h�y�Rl�Y^$>X".T�פM3��B$ܓ�    `�03�d�����o���o�9G'�w㫱���)���έG��m���?�qM�L�����j�������p�몠Qz@�%�\E�939���wO�
���7�Yo,���x��%�o�K��!é���E��,���)׉8R�hJ'��XIA��bJ��_a��F��djF�O��e�.������]��`9�@���W�&cU�Q�hwT� ���U�M*�fX�Ӄ4xA�/D��Ʌ��=-5��Z�ࣶX��&���՟�Km$��|����L�6��^����E9k�0������9AFF1ڰ0�>8"F)�[���\���cԶ��<��/���Ňx�Q�����]�[h�a��]b���v.��,CBO:[Y����Fb[	l<7����6	*ueh����٤֮�Z_��L@,��H���!y*bO� >�B��$m(ԫeYԩ'�ų�^���ϯ�e�X�����t�L*ؤ����`�`O�
���A"��j�a��؍������D/�gmL���Pm�FýT#�q6�f��ӱ��VRY�T�f&����Y~i妮�{.�-�������<X�!�[��O�itU�,3YX�h�����h��0C��2�t���i�� �sD&��~g�F��ń��ŝ�
-�u����o3? ������N�$AJj3'����E7���p���XO���L��<�<��>�{�N7E�K}�7p؇�g�pu�v<�7�=-+xnBIO�0���G܁j]\1�ҩb���Xf�cM^��)�[��)a&Β�c�&�&GYn�`����`�Xi;�������������b���;s^�~��m����'�����W�ť�e�@��-.)�뷬9����7�Z��-.p��T�uB�[��@̈́S=������ޒ�,��Y&h�U�����j��X�t5��F�EgX�U�5�p�vW�y�~?���>��j�3����`KLO�J����(��%N�����d1M��A�7�ݕ���]4�{����<J�ʄ��p���4�ʳ�{M�@��
�w&j`L�)���J��c�N���I���׃s���RZ�M���l����dY�Ff�K&��z��'�v.�ׇ'���c;�~l��hV>�Q�NCe�$=-+F�>S	ȝ�$��M#=-+d�F�0d��Ȁ�2.�B#�6��i��*�r�l��9�bYc�8�h:}�3�FeIHl��H�����+l1;�F���V�����E��g;���*7>�y�1�j��e��`��������`h�,�6)���2�Ɇ�[e���5�g�g�x�&ȹ��&4�fr��6��jN��$��Z]P<����n;��XSU�V������EMu@%����q���c�h0��Ӳ�` �Lym�������v�d)�.,?I�Yy����	��ަ�2ㆇL��!C5���C�z(/�7>��x]�A�j@����ήR@���e�rP]�?�����ŵc�Jx�`���]�,���1fdx����5[���XB�v��f�����
�뤫t*s���J�.�(�**��D~1I���s�c(����%�Ԫ�>M@B��9�����N*�3A��?#q�`��1�Q+���ǋ����/B� �ن
,��,W��q�`�A���gR�=�Zt��̽���d��,-��ś6W��-��
�Iy7+F��S'�A�o�s�1�ӷZkI�T/\l?�d�^g�A/��Ӳ�^�%�&!�c6ǐ��FE�ڌR2vq7�t��v�e�	c@2L���VJxd�>��$�p�g��3��I�����=��T
���h����jtW����FcXu�1+���e�&@��cOKƮV&�����_���������S���yY��Eb�
�P鸹�pt��G_�J���6�ʵ�0M��q���'d�jH$B��|�����,NCŽĲ`yv@)`o_�T%kh�� �;?������1��c/ ����q����$x6���o[��!����пСq�=�nn���^L�a�D��2�yL_Lsyp�yY�o�����7�~�>k ��~��L���v��%cD���4��0$p�x���ݡL	����H	2U��SS$ls�j�d�WK~�Jg`�=c��ZRts�xA4�2{��;w�L3�p���Oյz�p�bg�2�Ɋ�̳~{�r���!*�+$j���Odjj�L� |s�=-+|r�����
�t�z*d��[�X��e<TsOuy��ν�A�1��
���=�6�R�txP3M�Q4M6��Y�˿������!}1��j&�#�"��l��@��WU��X$ݨa'I�Bas
��0�.�iY�0�.d6]�����O�����d6]�ռ��u�d���3��kf����Y�,������ıY1�D�!�iް�����" ��&�f��%�Q/���b�8n���I/r��$	���jzљ�A �{ZV ���M/��v���]T�?U��A��2�S����}N�i2���bj.�
���i��끶I{P��Qx�Y/��MG |Gzw7��Be~�k7��B��>A�L� P�=-+P�,��C��6i�~����Kޙ����
S_Ǵw�7d�2B�$�]�	�ᚑdI��{�7#�{��:>��ߌ�Ǘ���X]�ʥ����M��#f`��3�|u&���l��4T�m���فHD�O��$���j0o�Ӳ�j o�l���3B�1{(nJVj���p�L�k$�@�E�~D/���C�I �ܨ�$�]�t�Mdڋ��^�N��V��_�50�q$�S}��A���Y� p�c&&*���Avߣ{Y��� �e�� �^�pښ���}:�u���8�LX�%���5�A�n3ӊ$2lA��.>^;a{�ݴ�;���*ӻ'^�HNZ����)'�����#�[�;/c��=�{ZV ��fY��m[���}�,T!�Ͳ�������I�^�åQ����j������y��\ۖਹ��9ڒٞ��N��T����,�'�g�_Ǎ	�I[*}�<�lѐ�NW�m%:����>KtFƿ`�x�e����bW+�ܶ,a��B��eL�B昐f%�/@����vP� j�ɋ�Jw���TN�P���!z7x7�.�"_e�do��A$�f��Z}QU�C�֤���8��.�娘�9�WAt(��i�Z פA�ۮ�gwT���İt�H��?Seo���[�������GU�>U����s�>>y��by�ƿ�H�Epz</���
��]r�[�M2��t��`V;}Êl��N
�kc���ڈ��d�����xz�_�d�G��1(<��yB鎣tȴ�Z-�A\��h�%�E���<�ӚD�5��ۂzZVR-����9���Rm����F��-N��ŝLl���8ΚB��^}R�밬���z�F	�I囖`ȨYys�����⯘a"��8^�2���G��/T@˗��(߃�s�H5->��n].Z�ś�4|ٜ�*�|��/�2�0�'Ff�k�� L�zZV���p��s�;b���u����FcҶ���?���ѷ�78��^�������l"��ƪ��G�1i���ӹ6���T�_U8�V��y���/�o ���9(-�W9H��s%3Ǉ'�_/�)��D��4�D�;J�;rz��|�3�0�ޣTo�3�� l�iYA4�8�m��@t�w�����ZC�6��?�U�ֽ����H{��vђ�ċI��*����F>{���m�-��@p��ӑ�;�&�h��Н6�CH{&I����6y�;�C�l�iY�3�2�E:m��?�<-~+NSm�Wu�׎�S]&�I��j�pC�](e�pnS�f��.ĵ]��qj�gz|vy|�<���5/�����>WWyq���W����Jm�T+x�:�6�A+�U_��]�y���e�@+��@% �	��}�7mc[���Ь
��7����*�d��5����]�N�	�j�t*2��Bt�2������mPXz�/��~����l\�c(�Fr8��*YDZ'�3��pl��iY�2���C��n�|8GV��Z���e��N�i    f�Q|�(�������Ԍ1��Q�ks�z��H��rL�s|>VY���w�W��O�ތ��G��`�2��W��u �fk�~��YD�?WoA�E,�z;S;��V���J�:ܲ��߀�R��)M�-�IǸmՁ�|y��l�a��-�w>�e핧�ZV��Mȶ��`��̺��7#e�t�m7p<�=��h̖�T�o��$�|��5�����,Wy�&������C*��'0�At:�5�]�A�@��q"/�<�`�ᥧe��@���>3�`���6�Fo!�mxQX�����IUW��kdD�����KƩ���-%Z��B[`��?_��&S��_�U��$��r��ܽ��6������jeS�fO�Ka����L�K�a�qB�Jw
�T-��f�1�:�B������g�KU���P��鯔kU��c���c��L8�:��@=-+` 	;�O{����pg��r1_<�b�k�O�~�����GvX�4���5�9�ݽ�?s摍�Nm�G�h����n����%)�`9�]O��p7y�k8��ZQA�?\�./���M&1=�;P4�hJ��J�ݼƝY���LA�.�\4�\ ��w?f��ӓ¡�	gǷM��!��NOt	;�j��V`�F�<�d�	KS�f-(����P�YF=n�L8ڰ%��p�4K��Y���s:J�I�s&q ��)��)�)����f�e��%�Q���^�x6�s�ON��D��Jm۟�n�ײg �0��^�E��{�����F��EB��FwF��=�&�9<P��f3z_�=�Y-ތ��(��?�� �Tk�b�����x-F�bY�~��&��b�A��:� �yz��kܙ�-�����v�&D���>��;��n��]���~U����ܬ,�E�On#�!�Q���}>�xwe��͒��d<~s:>���7_�������WW����8��|�
4�h쫜��9�y���.R���A[�����ѧ�oŤ���d���bl1��̏����W}TU|"bY���%�_�ag�t���1���p�O"1G����V0�}��Yt\���ʠ�_IO1:���Jzm=�Z"}�L��h��F�^��Y��8�`��P!�����زV���e�����3��)�%ߖ�pT;&�W��N�}S[
u
�U`�8�?,
�w/�E�E�c`�Z�4}`�L��b�"��B����U44�I$��(�nt���HvCaJ��4T(�jd�0	aܢ����+3a����6�WWª��W�'��2��P����)�2�+ŧ��3����@�1��VjDk:�I$����rix8��z1�XI�7f�2}s&r �P/���
x]��e� �R�w�??ُӹ�#��
N�*+��j
m4Y<��C+��XnP,rWG�v�'��_ɸ0�W�(��iG���0���.���z����) �O�O1��KM���ޔ�r���V�R�;U�o�{��*�bi�%�L�Ϣ`����U�@��Ȧ-�kD�-�7ѯ�Jl�+���z�Vя��A�ewH�SՍȬO���Qn���O���&YW�b�}W�4^̤1S={Ŵ��߇L��*6�d�Nv�%��>%�VLv͠M��YzZV�&�Y����)eBms,��+���E��/>��RPϋ��N���t���$�;�Pm�Ň2�k��]Gv�f�;s^:��UA}Fڶ�O�� *�Y%��R���$㱮��K6�����/j�!�Ix�m{�nyz�1v�nz�����V���P��d渏�z��(8=n�"G���z�f9��![D$�}��n[��WE�$2r�"��}ZW��j�0E��A���V�7���c�����U��NM;�ə��I,�����hɜ���S- 2�᷍i>���~+FO�h<�+�Qv�Z����ċ*��F�t�s�V��Uu�936���U�	,�c���&�2�K������FYdi�t���^�/�0�>�*�cS��y"$L�!�$�)�`���@u�5����qs�yތ�%n��$1�׸S�H�p]kU_W��}�a�w�l�B1�0���n�V.���)���(��+������S���OT��ro��6`��AH�Vqz�\h���:,�Ӷ-�g7_�>���x�ڴd�~������!+�e�R��ט3!CY���:��F�p�ySzM���������]��uH�(�9�h�,"�E�K�,0ȅ�%�D�� ���O�`��2�y}��y��ˉ��ضF:?�x��*`�e���5���߼��~�ܔw�����z��j�$�:9S��\�7N��a�*_�0�o_m�����X�ϸ�,��R�Ma�y��=��'Z�ʅｦR=7o�T��+�k�U�!�o���5�N�_�������ʥ�|�g2�B"]"Ij�8��I`y&Z��W�����w�Ï@��~�x�����7�~����I@U�3h�#���dzq���|��o�y�9��7�0æ���^���<� a��-89��w;�
q�{���wW�1^Q�B|��0	\r���j���ٺ�>ǐWxU~�/�`�&�6N
��>U5%�:}\�䓼)aU6U�[��k̙��x�$R�����j�&]�g'��-$��£�A7~�Lŗ�����`^�.�|��A��I"�>ᨄ�U��u��	���h^�`�s?[w|�*9��.5�F��۟w�xD�e�5����|�)6�WpW�G,U?}, ��%��ʪ�����s�)���H)��{E���/d����@S�B�q���'�-�?��Kq_�x{�~��)�}��[e�h�U�^���]�G�\|��rl���7O�_����f1쥾���>2*p�G��|Ԯ�	U����V�UTF��p�/���w{�Ui����[��H�mY;�G��h�M�٠!|�{�;�vY�f�]>Qʔ��j���rm��HfRt��xrx��Co��@��D����d$SJU�8��j���u�n`J1|��լ�'��͟,U����l�m�庽d�`�L�U-x�����'�ߧT9}�'��:%�V����;s�2tިr.��ܾKXHe�=j�W8��97��!o��#�2�1�8��o4ޒӶJ����1D�z��S8�s	��r����e�8{��~�㓉6�������O o:��v�֕Y̷F`����Ƈ�qe�8��uy,��a
?�Cmn�n[�}9S�w��J�xԔ�p����T%���I��|-�E�f&â0�ͯ\mRq{/C�E*�r�����Υ���7^O���6V�Ǚ��|?r=�g���o��	�����d��yon��@����RM	Vls�Qܯ��!ܯI/e��p4!��j�W��-��}Yvk���9���&a�f 74I:|�:�TkU�nt�ͅ%�+&q�fwd������N�T;�j��-Z6=j��+�����r��/ߗ�B���d�`��ד\�f���_�Ä����D���e�h��j՗�����8Ѵ��h���f�r&�/_S���H�Ԅ�Pw��ɩN��exwukE�D�#,s�r��y=��ꗕTh�\e�7��mRޤ��_q�)ֲ`93�v����x���D:?�Y5�k��0l�_m�ULP��I���l��*�������QR/$���Z�XPs��U��EWͲ�O̙����Rf�k�*nk����L��e%_�	)�-�ۊ��-��x+����Z��Y���`Β2]����YJ���~m�	g���~�&Zi�_�����Yŀ�_��l�
�!Ü��h��y3�#�/P[h��S����/܂�_�m�~������$䀲�cQ&E}س�/�j�SUXB�&�N'*��*�m����k����H�*�iº	hB���܇�Ш�n��sWr�r5��7�%��mK��T��� x�1���YF1+r��$���Mňh	;�w�L8�VN���>�U&�D$id�p�nI`Z�����ݤ�C�_��+>ؓ,��_����p�wfp�gh�_h�Wh�Oh�+�Vd#�h�	9?9��
[�(�w���W%���s-���6�Q���&x���C�=���c��]���j������ `   2�ɀH�&��X�Q$�d�ڜ8~)��x��-�(�1����54��1�~M�g�o��	�.SӮ� kӮD�k̙���
cŃ_����������[�      �      x��}ٮ�6���+��K)J�7Ou�.l����E44����"ϡ�y�{k�igڱD2�"!�e��:u*M������>h��]?	����swa�ݏ������G�H�/?eZ��йCZW�%������o
������{�s���&���F�����������3��)�4J}w�߿�՗����/����eXǈ>��m4sC˼��rx�ϣ�%��ui�v�/��fhy�i�~ �����8�^�����iSԥ���mX�~R� �[x����e��1�@s$4gC+꜒L��M�OuٌC�}�3O���2��p�p���]��Z,��lʍ���o�����A�5���]�e=K��ژ~� ��$m�=����8�����O�D.}��on�����~���2���}`�'�1v�e.�eɏT�*N�0��*���2}�D	�J����^�Mޓ�1�Y�qb��6g��j�����w�?�؅�FCQ
@	6��Yzi��SsF�9#e��цB(u���=�����$46 0��Sɒ�����~���e�v�	�2>A�f7�3>�D�I�i3��&�n������X�L�t���j��z�7Ui[�̒inV��D6v]�|�d�~���sf�T�ǔ�)�6��oӸ7�CM=g�Mefv0>3�G?��;�1�L��> N�J��d|v͙2�h�&��7)�����=K�%1R�X7���e����s�x�q�M~L���i���so�м�RrHN�.[�-Q����]���Ξ�>i�me4;�8�K���5�2�J�e`�l�`�t�.�"�ڴ�>읔�^�/g�(��Xl�5�iZ�,������B8�J%���(uM"�eҹ�X��ypcw���H7��ѹ��8�5�~=�?�p��(\M���!Թ�:�u���ӯ��L�o���
C=��=gdYu�d��p��l���a�om�O� ��`{t�B�Z�,�OB�`�d-,��]�jV�7W��,���1���2c*�R�"�
M�%�`5�p/:��T0�i��|u�B��g��u^����7��l�EX����IW�.)��sWZ����x��
y�a,`�#m=s���z���o���K�<��#,�P�L�/q5?�
�A�R܁VK��e`h�����g퓆��>�`��L��j'�O5�B�t�v,0r�����z>�����)$,��]g�SXj�1]!T�$8�9�{M���΢'H���(�w���c��P�=��a�ci>i��h����=�@B�H��`ݜ����z��X`��E���]#"/g
y�%��:@��Vol?�O��k�H�5 w��'aM�����,�ѣ�}��¤��g�q:��j�K�h]4����vn�g3F
/E@��q�)wL�J_�����97��է�����;�Q����4��UYNy)��oCRcbi��㩡>Y�����PH8Bڬ=w������=�� Ca���8���oB�(�l>�>��ݫ���f(1�!鹝"��K�U6��&4��A$ML1�����:�<��%L0$k����'7+.5s_�J�� {��.�]���l��P2�H����!	r�{�ˀ|z(�9᎘�����$������2����1��Y���(����+��KN������ye?�%�A��a+���34J$c���M�����Vr5t*h2�hA��&��na�?�1��0��t��.Ӭe�������!VXܹU?�j��_��V��s�YEq>�{��R2X ��^�ȔN���%R2d$�l6���q)��OU��:&UY	��x`ބ�L'gy�4���)Z�c@�O��l�&������J�f���Y ��΋��y%ϺN%HC@+ q�i��YM�|�hS	�v����r�id��E���S�1cw�l�
kj�㢁����^�T\(�q�K�U�����q���rWj%M������%�B @C)����v����V%3d�F �}k�g�?���w^-�`�d ���؎>������%<!�g��q'�Pk���=� ,���{�6ni����:Q8=�T�J���Y�?���#����\�*��^�� �lƏ�a��/i1,g���y=$r?[������Q����r?��E�Q�=%J��0��R�l}�����g�*Q�=�$x#��.R��V���"RDf� �+�">mlf��,�y]V�=Bg���n:�Xf�o!�pz"����ul��Yt����#�%��n4�ڌ����IJ�`���Ʒ�uxn�+����4��r��E؝�J!�q�فL�+�j�\g	o@:=����nѮ�����~S:����ir��F�D�l���RH9����o����[���Q����S@�o�+Zt��2����ȉ��>�>�!��Od�N�
�z#����N.+l����U�N�D<�fs��zY�Y�λ
��"�0�'��ѳE-D��Dr:Bh��=��NL���*,I�A�*
�(q��Շ9D��#d���{>#N�B����%g���p1��4�[��n���3t�!���_Qf��^*�����R\�/IѼ<V�
	F�@��-�p]g}��QI�#�	�ı)���K.O�E�d�w��ژq��h�?Wp@*O%� Xf�/~�$�s�?�PKvL%��G2�5=}iִ=>O�g�V�� `f܃Ɖ]�63��T��,8��j�nq���Q%��`�Q2�f���|�s�(�U�7�,5 ɸC�f-V����Q��#d0�;�ܙ;�t�;-�»��
�r������0y2gB�M%S y��MI���cg(Ta�H� �E��|�k��U8�D3�w�$l*�`��
��p���q�je�`��J���w�bq�{��*�1|���*D�J2Bnq��.V��T{��Q7Blq����V�Fr�Q�X#��[ܙ���*9�8�Q�MS�}C`�'��4˃�vm����VZܗkA�N;�1��R�ZSI{C0���:�S;��w�d�J#�`-�-�%!���*B�0lq�Y�a�������4)��C0 ��(*2��s֗U�S%b����`K-�6����ezR2�`. #<i�z���{�T��)�`a��=dk��^Tի틩P�9�h	Zi�ƃmz�F�
5%� ¶�/��x��e<=qJIh�`4�F����d�"0B�;�Y7�����9 D�B ��t4��E�D_�B�M�3#��m�@3聗]ջSa���u���.4��j�Y�R�;Bzhs׹�1NX��<`5�mJf� -������ry�ԙ
�F����ܷR͜{�._�7L����F`�lOا��툞޸
/5%u�z�l�N�Y�����զ�E�|`l6w�ƅ��n�>�{����SY���uvFB̹�>�{Τ|���Fr��Vo�|��t��X���z��!��28�W���	h9P�i;6�$v-Z�a����6L���v�m��/�d7��z��z�"tm\O�X�$~�L�5{�����2Y��ߞΏm�����oi?�%�"�׹3J�lvx��g��n���.����4Dth�s:i������$h[�p�aC"��;��:w�-�S]�*���L7��c?0ő H&���Y�+����W�]��zJ��C8O4q��hdi���d���ʬbL"1���A�q.��}���x��G�70����S�x����n��%eC��ūa���E|5�.�����o���CD�Q�N�h�وˬ�Xv���)Y$���ly4�ܪw���G(k:������՝��E��+�¨�7f"<�*�Vt��s�iao��H�~�/~re��*N���%|���K	N���̨����W|�2�]u)����/���u������&��7�o��^Id[]��E�����cJV�'�1��5@#���4��z����r�L����c-���9�K���������ْ���hE��k�^��H����i7 �"��J���Ш,�눎�u�6;�R�����y�$    �T(��9k(1�j��v�`�?��ɷ^<��l�eK�X����%�{�NW���Jn
!��=�-�W��U}���)y*^���u�9/�y=�[��˟�?v�p�d����o���o1l��o-z���u+��=�;�����>q2��]S6<�Nv��Z���q���9����(�xK�v���lT�fDY}ޛZ
}G2W���l�+��!k	�H���B�ْ\��E��:����F�����DS���֡֋�H� z��	�|x9C�
x���x���ȒSv�hm_gb%}1�B��a��hW���s1��2��Ѡ��V�MFl)�Ax��H֔sY�wh�r��dƢ]}�s7���"���\�>�/���JNۥ�U}��6ô8>�>�Y
oF�hN���p�x�>����k �M����u��蛅gN2ٮ�����2͢Ut�m�ځ��+���$��S}�WB;��w��ԃ$N�)��9f��s�JUlbQ��B,ے<�[D?:�+�WK�<�Y
��az]����0�6�����<����7�QK2���t �
aY&���a)�	�Ek�:�Z�-M�_�֫��j$|�E�$o��d�4��g_�jN�t �������z�|�I!l8m��\{��'N��g�5ă�sEt͒���E�9��UF��)���L"Z��yp�ƣ�4ٵ&��J�xt�Iָ���d[6W��f�U��,I��Jt��!��ei��be�d�a���p:�vN(��������6n�و�ْL��hI_gjAq!�+Ch[�p�":ԁ7L)X�������`6"o��Z�*:���o5�j!����~�(ХOOZ�
��A]4��s�yv��ѓ���	" S4����xs.dDm<(I�K����?�c��aٽڡ�J�j��t��vA�98J{Mb�VH'z_���A�Y]r�� ����o�^>�fclI�P��C(�7e�F�SV�V8$�IE��:��6�i޳X����3�A<�S&6b��dcC.��a�G�팩�iſ*x��=��
sD���NcYI�m7w�Q�%�,E�;p�!��!2O�$[�
G;��6+24�}���/)=V���7�U[�ȡ=��0���٤���U�2���uN�z���������d|��I4�C�]�����8)@��p�0�};�#�&�w�̈́��n���\�!Z��y��LQ�s>�g+	&��}�ñ�ZCk^�׳��/B�D��:Wu�tkyA�rf㌿/����#�oK�=:�������K|vO*L	�D/�:���t��O=�J�:����u.C;ȴ"~I��a��>v��>v��Q�R��{�����(6�%�S����ӺOs�w�� �J4�C|�������[���蛍B���aD�9���VE7�4�v��.�A�ܑ$�2�2_g�w��)����(�	�E'9�gfk~��N:JA}�� :Z������l��䲿�d�Fr���.r#��IƎy���o��,05�웃�G������u��%���j��#E��i dGt�ߙ�����3U����Ɠdx�D:x��Z����W��#ёh6_爘I�{�\��*}���1u��J�;%��XPbQ^�-�"��B�f&��s��ʳ�'PiJA�s�r��iK�5/��L�ц��A�G���j�2���re1�$�S+z�����Ŷ«W<4j%�@ȓ�d:8te��}`Oڹͻ򟽹���D�IP�Y4��:xAn������Z!�;欋x�A�����:,~���N�D��0gCt�Ê���J��3E�(��R�Q��K��iE����3#ٶ�_g��� �j�ш�kt+0#�[ǆX���ߠ�CqjG����l�+��v�a���tZ�d���V��\=����_����#Y��~{`(�N.C1?�9
��H���U蒦t�z��'�ٝ�.�0 ��:�j4�*]�؁a�N���嚏Ge������K��(�%s��i�T1��Ͼ��,��TP�x	�H�l�w�<�qU�1��B ~�1��l,�so�x�dH�TLf�C�\���������]s3���ӷfB������Ҏ�����;�n:G��_�m��+x�^��y/���i�e��%>j`����C'��b�����IXP6�D�^٥�n�E�?�O�u�"*Af��	+ˋCB]��ĥTy�]��fH���b�����M29���eHX��.��"��uH��p��f�w�8�.*�V_��	�=#p�K�7�[��<a�4g�1t���#�g��f����0�)�Q���%i��uY��{��� �(��
u��q2��V�:����5Sю&U�	���e;$`�����7��M!��op���1�L�D"j�f\,v���X�2�;L)��ƀ�U��|6^��~��K1�ǳ i!@KV�*=a��N�$��8����R�c4��<az�m���\=���`�!L'
���1�Yp���&���D�y����SY���k��w[m#Ѷ���rؽ4�&w�{�)`�;Ni�O�������q�4�`�Y(��r��w,��tK���-��j��zc�7�G9���� 8G�1Q6F6�-�rEb�jJ��ڴ���:���=Q2����Lb��:t�����6@_Tro�L��R�`�Q�s�Ryͅr�ID�6,z3���r`�F��މ����wHm ���s�].ӧv�r;��E�6������~:mHѭ��NXk���5�uD��
cu���,nI���n��3|,z��	��|���ߢ<�i��!$sm.���mt5>���|��܈�����{��tr/w�ǝ�r�0Da5���U��ak�>�{f�q#RQf��^��,�`���&�<j�J넹]iϮ}JI���V�q�RF�L� ^ �IR����	���t6�(7zaɾ��{_�����6� �@�]'�2�D{�sfL	CL**�ƴ�0,o��8�T�����cE�,��D�v�ճL�����G�nm'%@�kQ���\+�M�Í�>T���PD�uɔ�Fg'l�.���e���e%�/���B'��7���%�bf:S�o��E�6x�(�י�|�Φz6��P�{�68���C�L{��}�z
��ܹѷ�%��?��?�(��%Qn/�~"y��-��)���F��r)��Z%�ر6�o��uܢO�'��S C�s'��LRv���8Q?�+ןa�L�l��d~�jU{X�p>���)U]G�.�|g�n����mO^�	�`!:����Q4}�|Z�ģrK�Du78�Q[M���$޶��;N,J��+%s���eHR����5�G=m��j�:䏿mm8�|�Q���@�KD570����S�h�ա��l#���$:��ng1?�e9V����Jv��(��#�J,�����twDGY�]n)���V�0��z�#'��y�Պ�l���mu'1���@��!C��������]J�~�j����_ʝom��	K��:�oX�E�eo�/s[�z���D��bZC��*
��.S�)]��(��Y	cw�[��Y/�1	�ˋ�'c��j��̴��Jb|ʝjX<5��ן�.��^�ܭ�%ۢz:a$��Π�C~*��\���]Q\3v�<�v�O��M��O���nt�m�!B(J��.Т�5�����6`�p�xne���Q�zS��}���١J���&��:a�<,��~������n����nXv&j�a;ffH�ʽ;�g(9]��j���%�nX��X�~k���_F�}-j�k���C�;�cu�z95�����/D;ldV�R�����������2p�GQ��}���>�:���R�T��~+Y¬�d��O�ʑ�y���X�t�?��se�TtY"nY�{�z̈[�kz-����G}�n�hܐ��@D�w8�J_;���s&[	��%����L�����eh�g=�n߬z�{7��<�v����k�w����D���:]�[�D�w¦����v����vf��j� ��ϐ���O�$�w�uX��\�����   ҕ�װ�XԌ�v+
˸����[	��T�'�k/�e�1�!��!�LT��j�Qh���{��Ts�&�<�_�2rp�~���q������]�A���T�FT�C���~�v��iV�&9�Չ*r�)1!
��1*!#�<�*q�x������P"G$~E� >[C�X���BX����ȠhXE19���N�ڎU�z<���QQ�ے�u]z����dD�M#�¶��Q/�_պ6F���k�X&���x3�q��D���Z]�$�X�p"
�յ�~�d��cGԈ'��'p�;s� �<��
pp�f�ء����S'xid�4���E�FiTw�*7�aѱ��U�H;��TR_���$�P;a�b��C�L;�7&J��D��{K�uC6$׫��B�V�O�N[�ɽoM2��Ԓ
7�ex�3*wb�(���URf���M�*� ʲ6��OF �_}ᖠ�7��R���a	^/mm�m&��m��Aܒ�9�r� ��K�6�cPy����c@盤>F�7K��uleQ&��nXAz�\�@��E�Ѩ�-aZi�Cd�+#�?��X�?/(�u�9&&.OK�o��ٵ{K�%˺r �qōj��ݶ˂`���a|�8f����i��E4DF�/.S��皝�:��'��}#���Ot�r��3D9�CP��Xm;q�ii��� �(�wHv,nNKX��U�M���B@0rqc8�<5(KP?y��)q�t}���������G� ���T�{� ̌X��U���m��W�%,�M�I���T$Q�B$���%�,��'S��	Q�C�3�;� $К��/��ٽ��)Sz���ՎE���֤Ǹ'��E�ʞ?�R.yDX[[�~ٴ��0��p�锞���_(��)�z��I�^<��6�N����� �:8V�I��Dܳ���:��6Q�G���q-a��t��#��n���?�QE%��q�̛N+���� U�($����;Vǲ�ꪄ����SH�8
��⮵��4�䉓i�/(���/�o?�J��8%��z���a؀-�>���^�p�]�Xj.�Y�e7�t��:�Qf�#�z��(Iy��w�Zx�%;�Kq'�V�B�������C�Պ[�@}XA�2�����z�s�=��K��6�����i�sԟ�YQ��0���f�o��dϧըR���&qK[����������4�J�"Y�m��iZ;��$����?����?�^RD�� �V	�;w��֢����r�"�l������F��a$������]�Y�~	L\�7z�T�������x���Yu_FG���u�=n��~Q���g4_�ͮSmPIp_��$�2{���mF%�@0��.a}�1�R������8�~�)�"~M%a\q\`.ǘD/��
�FX��.aE_��~~�U_�)"�T�ť�ǁYr�|���&P!��7�1�����[�MD@M�ҖA�����(-?#���P&W�?c]�r	K��i������%m"�hJ��~9tգ�|n0�7`W.ܠ���������.�      �   �  x�uV]��H}���I�Q����RM�l�)A�~��[��8���M�s��9�j�_�=3>d*�E���%�u]%i�c���$
��SeaͩM�ԥoK@�Y�GT;��m�;��ښ��"lN��]Y�1Q\�8�q?fy��Hc�6�m��|�9A�5� 2bn0���%�tѼ��oq�o���'�_�F�k]V����َ�f븈�W����4V.\�{���@�VW�;M/���5@IU!�I��Ug�²M�{�����hp&���y�S�������<��!?IL�.)���^=w�͒b�Ckj�NL�C����J�}X��'�̛I6l]d���n߽�-��-�>�ԇgǑ�Җ��!�J=��M�'0s��W��C2S��>H��F��{ޭ�yG[>������C*����)Iv@b I~܇�����"+9�Et������k��2�����J��	ʹ�m�����@��;�pӽ�HS�T�I@��}�C�[���#sTt��|{نy�זC�T	���KX	�%´�5�������9�<��  �oq�
��`�Ug�,��<����F��Ԋ��lf��R��~%d�J�A;�����{��ЇC&X���A�U_�4mHۆ��D��>�@���)��ƈ}H]w�%1Ru;']��ޠb5F|�&�L
Ff��$�	;Q¡*��Ӫ!S���Zl�U��Kp����r8߫� Y����ű��G�H4J�$�������|�������!�ݲ�<�iA��yXU|�������NH~Y8��C�L�0��̍v����`�
fN����F�)���:�˼ltjL@`�O:��#�C�������<F�g�QXl��8������w� ��̗�-�ڿ�T�ː��'qolt-^5EVo �ؔ�S�}���\ 8�Ҋ'aވ�F�c�d��.��vC�z<��Lqg	{�>g��/���l�s�eN/����^ɽ<�yJ·�𞔊��E�B�V�b��G3��|Wo6��hϒX\a�M�9���"-jC/��xz^��Pϗm�V<��P�֋�D��XC"�
_N���(�k �M(b�ղ�K!�'�������d���mq�({�{��㄀��هX>b.�d+�`���N��h��m����@�����|[� Uomn���m ���#�Wׇx'�������uڪ�7;�n��q��h�xQQ*~�˪���<�В�(��
���L�E"��������1      �     x�͖[o�:��鯰�<�6W�[�I��M��h�K��`CL��`B2���cr'�i*UsQ�-����v�B,ش�!�=͊Y6�Vha\��O�?ף������十���ʸgU-�A��4��J�uxqQ�$fS�S3�y+�)˜�Dq)̒&^\Y7e)+�E:�2n
&Tݭ�n<�V���zBD�b�߶N�&�w�+yWQ��j�nZ� zdd������`�۰��w=����F3�;&"G؝��=�������ϣ���d�u4�ϰA!��ֿz�']�J��E#��� `k����z�~rWaa�D�o�q?��1>M�<61�&ϗ��J��'�o���s-b1w�J�k���3������q�H�w��2YԴ�d����ˏ]�m�p�Tj�*�h���	L�S�jJVqIA,E��j�r5R˫G�l
�h}*>;\G���}~����ő�t�\��F��F�ї�!���m|�c�����}߶��|�T
���y�;D8Xu�)#����Q�,��y��I@}}G��j"P_/��L@��D���z�����L؜��	������5�>���SJw�L�&��·E��%c�J�Z��E�+�2�5�v!į��������C��L�#YY������gEƒ�Z�qY���3
���XQ��>�z�L1jXۓOҚҔ�ם������ҝ-�Q��Zy�s�y��ݞ�u牠�w������jfC��5�L��.�7���Ĭ���!@�&m�  #�1��L�U�k�������O��*��D��Oy���|��P��H|Ӳ�c�iv����x �g�S�7]PH���o

�<
���Gͯl��E�]'t|3�c���tm�Ac�}�}4��rs�E�5l���E��X�p���c�NW�J/����0�GH�"b�λ��!tB�7��z�x&6:���9�1Z}d���o��\Җ�N�sd�?�h�7T��Q�fS�H��'��$�ݵ���S�	�u/D�D6~}��x0�������`      �   �  x�ݜ�n�H���� �4|f�J&�f�3�Y<�P��a��$��R\�;y�w�7�'�"��ْ-�TjK��U0X��eFFF� �(�v�����"4�3����U��쬶���mӪ�B%�<-�%뺩d��e|�K(�įd�t7��S�~�ʕ�]�pK��� ��!��,L6�o�gS�S�0��NHiS&;4������jԮW�^Dש�
նQ�����h�۵�]%Q�h���w���v��0>h����m�x7��Ke�jU��kUqh���������P8�@$��6p�ux<A&�c(�]��z�y�ٙ�cCd,Z@���W���i�,5u�4���«�~�_������6C�bm����o��;m� ۉ��xa��s��ѹmv��m�m�Vd��F��7=�}js����;* },���O���P��y�O�>�q4�qWwvv�D^��>����1NsW����g�v�vx�z��2xm�M�͇��s����� &�XvZA_��d�K?WO�v@{Q_+�*��{߿�"��2(���d���]z���H�.K�3c7��FlUwnW�����o�ې����a�����z�M�����t$ͅ�G�:���hȵ��@۬l��b�cZ��3�x*PPj8��(R����W+��y8�F!?Wp�-ҘQ�Y�[q�Q~���w�o�~��[2g�l���=ڎ�v�m�r��?�'�w_�_��S��@�V+)[���SG $2�3��_��{: H��I�T��6h.��9�ޚ�>'��9z�̶I�x7��#J���	�
n�[����]UKH}�d��G�ȭ��Jdk�J�Dhu�ƺU[�/�l�������;��(�N�M�����{��z���0~9�lױĥ7�'~��!r���Zӱ0�P�a��lZ��a�i��w�6,�@᷀��?U�������������zf �ōN�<�|��"��S��Y��ʷ�Ou/�a�u�����Fs��
8d���׹��D
���Ʃ;2���s��*��(�����ߩf��U����!��о_�0�:��~�v��T��y��?@o�Wi{���fkT�=S���-��K�ɹ�+YB�Kr��Rgu�+���lA���{:hi��E�!E���b��������\���\�uϾO��˓�+hv6َ�o`Qak3��,�h^�%��!�~D�a6�{��������u������2d{l���c�g|���f0�������3�x�O���m�k�$���v[
O,Z�l��]��ӗq�|ݗO��g��]y�ud&��:�!2c�	�S�wL�����:�3y�*�� n{��0b���eG;G�8��ło`��P�g���/N,=�A��5�J�yE���bq6�DF&��D���R�(�3i����[On_�Xv�Y(��Iz悪y��N�?\�{��X����S��=IϏ����X��@�Iz�:pFE×%օ2jmY�LX�Y��DA,��������b�u��N����4��G�|�����F�S�ь?�B2�9�Pb�`0��|� 9�tx���_�.�x�H�m��"aQ��W�)5���N�B?$���UE�B�Xz�*���Q�J��N�2�ZQwh�u��>��?�.0f� v�ZE�DK�*>maa!�l���&��H���,�4�A�3��h�7�U�V&W��5�-Z��%��!�f� IS�/����7�Rf���#QM�����O_�0�Fݓ��O� �T޵]N�/�0Օ,bb�J�V��I��SaVy�q���վ��A��\d�+ט ( ���>O����底m����Sk�]���A��r�1D���l+�D\�Vb�Eʊ�6��L0��`&�H�= 1���!J�.����[�TO,����w�T�Q�݉�BJ=�l�N�@��t�0)�;:Q�%�+�. qm�d�#	 &=�}�M��x�%6��އ����$��k�pNSY�7B�l��ʺJ��eM�PĽzJns�z��@]MJM�b�e�J��(�����M����OT_�J�����fS�`v�A�Jnz�˙�Cdf7�ԣŕ�HIɾX\���j��ڒ���{3��Е���Q8S�xL���VΚR����H65�s�d�Q:6r}�Ź>٧)cwTN�?�j}���5�KQ�s|�Q
!�E��Uk��|�p'����IY�b��WPS��L�K��kw��Kn�����&��Qܭ>|d������5�x6;��F1z��tV}~U�{̖�e�EцP����.����7L>�we'�ٺ�`�5ׂ��ϟa��Ni�􈦼v��-wo���b�5�2��!�ms)���F.SM6���ދ�V$�$�ݥű>Y�������+���:���d�	_�,O	���0]T�3�t­���i��]��2\/U��a�$�� 5E�� ��'�o�"��ԹŦ@ 3��Cg��}�V/�e��l�M[��Щ�ҢN�K^��Aze���8��&��Q��^L�{��	И�>��N����h����� �`&���xW(�^����1����]�(۽>��� �9�����������t��	8w=���=j�ׇ��CX��5��#5�݄�׆�l�qM,#���bG�%o^��a6ᇱ���m4�j;�	��=� ����w��P����2�fӈ���)<�L�1z^H�bY5�y^��}��/`��j<�o�$�[`15	�0K֓w�e%Է�,�Ա�[<�Y�z�0E�G]:_��ϑ������Y��
�^P�p����[զq)?��5�]b�(�U��,ss���|�Y��c��}��՘�06꿀:�=[�֋<�3�d���sI���G6_O��,�XT&��lK=տV��ͷ��U����c�q��#D�ۇqɛ�)��Q�"gÉM8I�q��(ivK�}��>��H�U]���M�p�+� ��d��p�^�n�&G��ۅ��	Fn�y��;����m��c�}!
�=n���Jp�{#�|���=����!o�Q�C�M�c�Fq�	F��<0�>q��w��C�o������[͸ު�+U�z#�|}��f�i_5�8��$Q�Uo��=��q���l��
חo�Q؈9{�i� �)ͼm��*y�Q���LDS�մ�L�y���ei�0�:����;>㣪��x���I��:H��u�c�!}��S��֧V�a��+@�p�D���4/H5�4OO�����(ԅ6�LUӖg��BX9 b��|r�VM����(�������|�.�w��B��~]�D���#G�Z5�l滶�yVS6��)D5A�<��x�+���a,�w��0�T��~Q�X�̒�A�U;��/�=�@t���1m5�bR�+�T�$S���UW��%�l��V��%+��?w�ye����KX"�޽A[��ʪV����#���E*3�Y����^	���jd�,��ru�{� �@0����A��RF��W���.�P��q�H��d�_C�e.�4���VW�qٿ����ӽ�>P�X¶{ l�$�'"M8���Ky�O%�<�5#3mۤ*K�|�C�A*��D�]�z}�t��t���/H�3�X�t���Ծ��Z�6[/EX�B�U+�p�0{d�Ew��t+k  �Yj-0���i�7�_��\j�T�c���O� �_q      �   �  x���n�8��UO!���Vq�b�I�˃ʂTO(�@I�D��bIi̻iٲ슝L�/�i 0�s�����qSX	  d�2v���ժU��~��Q���_�.�?������r�5.�y����]V�,Wފf.KYV+��{X�Ee�T$�ea�x��*�<���3Z	�P+<����ܗ����S+��L��؊&͸��o�
KKq�f�,s��fW�0���V�Ձ|�π1CH��&���������w��E�z�s}W��������@8�Ćk	�%�C��=��$�SV�j6��S~֎��o�p��Ŗq��m�W�>���o_Y��R��R��.�W_��N�3�1��Eڈ����h��=���Cu2�I��$?t��l��=�(Ke(.��}�<�����׷F�%���Y�fy����!��IU6~U�dˆ�눪�*�c��~z0��}���++$�%kcZ�Ny���Y���,��.��l��>����1��@d��	8��e�v �����W_�o����"9�����k�m�$�����i��'0��p�q�\k���.�>�<syA����%A֗�v~�п^����p�Uɾ}�6>f�0�y��;����ZgT8C{���xJ5�u�j�K����揵���b�)a�H�1E�+���'(e"y�(Q6f��]�?H��Y�gt/�`}�K(<��e$@I�簗��]Cɾ}s]�-dFD�j�;�3V�
�i�<����<MyU��(n�	����RR���}��ꚕj�ObW��?���qG�S^���ۿ��O8�)'�������0՟�s���.����� �r��F��f!�G�>��v؂a��rR���/j�:@���2�g�����A�Xq�*��kNE���NԻ�i�Z҈�	U��ŉ��8�66ֵ�u`��6�5���.�����k1GE��$79�f4��ɚF(��}��uLq�SN�v��;����d}Z�ܟ��i�\��a.d1Ao!�T�%*�5}~�G�&D��͑����A�)���yE��:Ao�"�X%��k�Ĳ���y��hoA ��b�b��(��ǯ�܄wC3����_.M�'ĵJ4�<0c$�P�|����C�ڵ����~�<����{�٤,�+FոiYLU/wY�Պ�<��5|��8��{؀'�կvL�5SS�J�$S;���HT$��S!wj*�M��5ҕ�w%x3냺tq�G��]�; �hs��١DV��G�2��x�oP�z�:�oN���\�
�ؐ�4ѡN!a��E��I'�a9u[��-�����	�I�{DϬY�}!S d�Ɂ]4���2"-s+��E�D�G�$�C��D*D����с�6���|D�*c,�K�	h�NL���SL��Ӂ���X�%�Hԥ��X�k�Jݶ�\.vSR�Vg�vd�LK�����T�_͓�s�-1�����0��l}� ʹ#l9��"6�B1��6]4���o�9<����Lb����7��G���3��l�Y�z�&o9L�"��.zM�4J�:\M�hs�&��.�N,MFya�r7xn�h;t�����!�֜�:�D�8��6uF��t�VͨZ��a`��!�5l�����ҧ����������)�Ӹ_��Ć@�'7H�v�ߴ>����7      �   7  x�Ŗ[O�8ǟç�x��sy��@b`T�ξ����I�\�&�u��}�z�]tg�<�������s|�2�~%U!�K>�fN2���w�W�������?���71 8� R��%�:�Icdt]���*eV��̘�/.�BD2�E�F����J+a��E�#���O/���*�s�l��^���q�Ȳ���n�W�x!l$��K�,��JeW�nŤ���m�=��ý?šw?��g��1��h�	g������եsy3�����s<����x�W�@5Ȯ`�c�>��$��A�{���ё{[<�g��Y�f�v�����nx���sx���`��)pF��Q��טfi���ʂFc1K���ps���;�N>�E�=��H�ɔ&��+�������x>�Tm�I�tY���xK�  a�?N&����Z9?�o������dk�?��UW&wڨH^�d$UmΟ�=�c��?���ʸמ���W�ݹ3�H�q�K�q�����N��S�[i�ǩY�$�UR:ún,��uc��=,em���A5�`%@e��^����} ��!�.�8�!�.�62%�º���qRF0}����w�8��,M�ϻ���������9�o9��B�.��q<1r֟����ZKe3��Zi��}t_�����%��B(��\���0�k\���D��=Id����  �u4+ee�*��R G'�UNއ�m{����\��wq0
	�����Hp���BWS����`�Q�VQ1��Y^�M�g��vك�����E3���q�ZL.��^��v�6�\�����������h�T`�>�D7�.y�����0;����+����	m�#�AH�p|lB�z�wh�鴝`�)�\Dl%ө��4h�[m�bǢ}o%Clل���S�d�DMT��됤Kà*�ӻ��H�)���z.a��Rߵ5�!A+*�z����6m��@b������ ��l3qq���`�ś�-����.֓��O�����H�RF���ǆ�S`���Kɳ��Chk���m);�����qS�Y�֩̚�?M]��Pz�jGnd'RƏ��c�������bol^      �   i  x���]O�8��˯��ڕ��v�s��Êa3ګ���8����8	�j���I[�2�TU��:9��y�&*c�U����:�y�[�h1���>���sry}����j��M39K�XJ�� �U�$� IB#ɪ��AME�EI��N0���S����o�К�����JY7��I]��������*��%���u��oa�q���/�f*hͅ�����e�M"�ѩ��B(���������J�zF�,ԘM��������)D[�Gђc�8֚���a4����⯋��k�/�h���'��o.�����ET����G2��5@L$1���T�J%X�z�./k4I��k%���JV��2o��TȄFֶa��|m�ϗקWw�77�����۶�Xn��9,�Ns������=>��:�z����)pEAj
]�,&��q�!��o������=�w5s�f���%�Z�h3�n�5w�����`�VB����_�trCO�����o��v��V)¤x�AK�*��3 =��RRx����F:��	��3*$K4M
�x=�q4��
���+��AUn�����!pN`� A�����A��l���b8OĢ�p�m)>����wuw�h+Iֲ�����˲�/w�v��ޖ�ZfS&:\�@7��p�=ãHVq��2��,FM���D���x���O#χ��Cm;d��.s�YeO�����༥@rp9���ۤ`���@˰\߸�`�8�g���]r�"��'�u-xGcΕ˒5�
���J?����C�Q:)XM$�����pN2�<ju����L�f�!�3V�޼{Tjd�l��M�5n�N�X�s�6��Lr9C��8a�����1�x-�P��G��:����+﹁��o���7>�N�Y�t�Z��{�b%�Λ��}HuC�3����3���N�9�q�E;�-ئ��a����7G3���D� a��H��l��Y����g��WH|��DXJ"B���ZC#�e^ޫ�neZ�Cܻe�w�F�����N,�,y�U���e���&��:���f�6�,�Bn�#����#�/:�$�H �����m_!i�2l������+'��9e�/A�v�+Ç���!��e�;9�<p��Ḉ�8*t_���r�
�i��b��F��\5x�v7� Ͻ�m�L��*WXr��3<j�彵�Zpڶ1�^����ҟ��.�	,ϰ�'ZW�ش��r���daJ��1j��Ӟ09���F�z���15���#��i_���� ���AJJʔd����^>Z��T���@�Fv����E������큧	,e�����q�C*���a��֍ޜ�e-�G?����� �j��      �   �  x��V�n�6]�_AdQ�@"��[�`�v$v`3� �LR6m����"��%�I�<��h�7��W<�����m����v��A+1F���d���2.on�/���dzyu��T�5/��"��m]�Y�Y��	5�,���R\Yr�̌���r�gE�W]w�2����O�nj�4��N�
�, �s�Z�]�?��TE�J�������!v.�{�|�ph;!L�q��/���F�����V5��q3��&�?2������/��Wc�h%h$H�_�!�Ds.W6*���@�G��X[��/p�jq��$�\;o��������Zy�v2�7LP.V`4~�<��9�sV\�G����S%�(*V��E�#�%�S.Z�Ʒ�+V* �X٩����=���U� /t`]ӱ�1�ewj���Iϲv1![������Lw�mt����lQez{�I���(=����#J���(�!�M�}�����,MCpO4�-O��f��*��Y.���t�Kz"������!tBM�cx'AB�j��%`���v靰ᅦW }�:	
�\&ŗ�4�D2�p�_��>���B�
�{N���0��խEl�LS�{ڦ�P5���&��rd��z���t��yq-/�KjX��me��Q��O�7-e.��#�]�]��Q.�);���7���,�c`���Y��2+�ꜗ�r�Gɓ��ç;#--vC�2]õ��:�.uB�Ձ�]��֙��{F���d�ݧ�U�4a%�*�D��(��_�˘��S���+�	x�T3��n�C�lȳ�`B�������|�[Ȧ�l��I�nM��A�Eʞ���r*W��S�e��!�L�
�P�w-�c/��R��4�):+�N�cՋ�;Қ8�#�LT�u�Q���>ʾ���?G&��k�
m��0ܙ���/�Ўb      �   *  x�Օ[o�6���_A�vx�ɺ+��$�ۮ7Z�$�@�"[(���)��8i�40�#����Wd�f8VBS"�M-KZ�ӝ�V������z�y���G1��N��M� ����Q��+����9i���/���t�����4<�n�Y����	e�VSnL#S�=3Y��ë�:��6��q�_0�!��C�7c���z@R�G�?�2�<�⥀*��1Ɲ�m&��[�}��C�p��)�N��$��W����� ���b����bu�Z��\|�.n����>-o��YȔ�ǂ��&�����h��O׼/����u������A
F��n��~��r���#k@j>%�c��#h��~Wzۈ��u\�\t��^v����Y�=
z���>����#�6�>�gt�Q�P����=��*��@VW7��}�V�N��'�t�7GM2G'����W��t�������9p\� ��ug���i� ��������C�n1f� zL���',�Qƞԙ�P��5�M?&4��pۆ튻�k�ݔ��X�8�y���y���k�fr8;����'ohlj����j��݌ga�Tߓwц�ɻTVaԯ��xJ�3�͋<��E���*��U𮠍��ue)����_� L����с,��Ɓ���V�NF�B����s�si��>����>��qB�	a��'/�G�ѵ�@.�&困%2��{c:}�?}.t�J;�L^�a�N��$#π-߾)֯{����j�T�Y61�M�>pRDi��񯦄<�G	�g���l2��r�?      �      x���Y��6�ǟ�O��y����o�;ǎ;�M�US\@�I�>�= H��s�	U��݇�̟A��(,"��� PUUӃ�.��9V>����R����(=|,�V)&[GL��C�������iЧO��O���s�����꿾��W?�v���7��D��W.~��׃����量��}��
*������E��e��y����jc׾(��[_��ך}P�׺���^��%�Nd>� dj\�0��?.���J��@��h��VL&,����<U��9Y�d2�������u~y�7������Ԙ1۾t�f:[f�t|�C���ė*	�'G���?�(U�t`8��%�q_&c+��������s��� #���7�����ǊO,l�N��}���T�3��
�*��3�2�S[ů��`�5��5��� cv����t-<�N��(v��/%��>Ӆ|RD���d�Z+����C�b�g��T��'66�ț�3���M7dv���'ń-3�N\��_��#�c��������ɑ�9�d)a*u58F�ͽ�~O^F�F�>1�&�>�K��[���0��|ґ�{�}�7� ��h���1��ɲ��[}�a�윍D霰9e៌(�7�ߧs�F��L&���Y�7��+�s"�KBt��&l��Eh�UE�M��?��D,H6D�|3�#F��dJ��g!��5�S�*��.������1c�{�����O��<��'����/NUy���o1I�q�����3q%�;��
(��<h�#|NO��I.u��8�:𴟽�JI�s��^Н���lwbqw��I'B7���JO���mᇸ�,�">��F�u��X��l�bq���IZ��n�]��0%�ԥ� �B�܉��ʖ�������+1��Vmʇ�Z�^ΤH8�{�%KK,W$��Ȏ\+C�x�r	����%Q
�������8�ᠤ釈s40�]���n�s$����(:ZdG����z^�.�.�E�O�tm3�d�1n5��"�1�NS��,�7s�L< �{�G]�J�Jf�+f[T����kXZ,xӐ���#Z��)�ק�������8j���e\����]���UM;ʯ�G-O��-d����
u�0z;#Z�%����Q�"��Ӱ5�����ߍPG�o� ��[�<��!�ī��EBIr�ud�
�����������o>?�	�!>u���N~�;�Dm�r�г#�����ӛ�g�Ը���?���> �!���/?�dN�M{mz�u합��LW��Zw_[�+�uf *�����q�������(�J�ɯ���}���/=��?�g򇏈�A>�����R>������gW��������=��>�ş=!*y�(:��͈�*����:�3���o|��������"�|��
���L��c��������|��� 	�x�s"M�>��:����珇�<�����V�t�w�]��8�)�r�I9eHL�3�a��X�	R�b��~����$�d���$/�*��2T�$/�+L[��>�O��_$���_�:��
�s������� 6S�H�|����K�r���3@W��S��:!�_��\+$��3*��e����6�h��b},).D�o���8�,�LK.��s��͋�����Gd]�)]�8�O*��d�>Q~�86ć�MH�O�aG���7,���� m��0��Z���gK����P�"䈕kJ~�[o��<���x&.]#^P���a�3+o�����Rr4I�{�Z��+Y��$q5�܍/�-<��q=��FQ��݋�`c,��3]#ڿ�w7q����ej�\��BJ<�����І)�Q؋�=şm����d�f�����9���BJ<�q�&��4�'4V�B��^�n��E�ݫDC݊���L��D�����r:�sA�*�7�������d�L$�8hZ\{�,ddR;'�4A��*uvrB=��`<�NK`�|�3���J2��T�I��k�U�;�h"d&�C�#4&�JH3�+���� ����� li=�x����O�l�Ѩ^1�1%m�°��k��s6���"C;D#ּ^��i�C<�/�*��v9װ7���4j�d���%�lu�B��ᛇâ#�����`�{Ј����p�N�!X��Kj�T���%����Y�'�ϭ���8�!�j�:Rzb����K;�y��$��-�DM��e���"�M�s(c�c�7�q�,.���d�ҹuK�,�bH�u��P�j�%��HB�ϟ�#�C�㿇4��|k�������G��N��V(?�e�_I�g�閃yz+�{�ʙ�V<I�㡚�r:]e��(0��S�����yd��Gn�_�w��Xg *UE���m���% k����� ��t��GyX:��l5��B��s��%��6�ՉPte����m�@y������8���:��w�i�V��%Jj�t��42N�žZT^*��\�UB^Iר5��w�hb��e�Q����:�M�� �)��h�zj{-{ѥH봩=q�
�t����r��d�Ck��;zi�ʷ�KQ�Qw��x6/|��|�f>��w��Oj�hйm��<U�"�ߪ66ڔ����D���d<����z.ԓJ^���Ij�d ���o_�^�=�M����Ht�U9��5��ܗ�ҶzR�L�j@H�ki���j+C�{ȵ�C%��xJ��	���QZ<f�9�&|�c�6��rh���;糁�6����7	�Tb��1�WQ:j��oٙ��̶��h"#I�d�i�}����Py���D�dN�H��}�Lě��-����y�C�3n� 31d���*{���Q��ª�:��Jˬ�\��#�N�_=:����a=rk�z�vNu�N�l+h=��c�ͧ�El��[X�<��e�q�]�24~~���!k����Ñҍ�ڥ�Wj��CVC��e�E��!��&-!Kw@[�ʸ�̀�9c�W�� ��\Ϲ�3��7�y�����6��%`�|P����M�H�\���v�*I"���q)�K�ET���S�Ԯ���.�h��>3b�z-��.i��˄���p��ވyԿ�������fB�(
�C�3h)^e��|��9�sLHzs�X�7��P�iB�� �r�q�H��\�~�t���>�����#��}t��}[�	�ȪI��A,̋�cJD0M�`8<Z�{�x��k���|x�ɡ�<�i�q�]�s�ي'Q�4��a����O��y����d魥N��/�+��h��,���;C�[���	9C44h��ԡ��謼�H�¡��xx{��ŜRА\�K�^�{z�!e#�C>�2Ҡ�:J�ȭϮ�
���Wĩ'=� �͓#�9:�V@�:�	u� �V�y4O8��G���Cځ)��)�t�'�i��΄�fBI��y�8�b���~�'G�����E�e�3��)k	�޷]�+
�#��G i��A��ӥ��#bކ�nT���!y�hlF�Ԃ��. �Lz� �Xy�}��~�ΫALv��e;�V@�;*^�����J� =Ҫ���|I�y��"��|#�=�8.�k�"��$��D
���ɠ���ۓ[]G���@h(�|��EG��+>�05�hoF��	!�s@�U��I}T{�[�z%�	�T%)#�)\(�|%�v����q���9b�����$����(>�E��\=	W���|����1ݭ
�D2Ԅdx���V��G��p�N{R��A'b>����DtՍl�D.ԅ\x����%z�����V�<x�U�v����P_�OC}dAb^%�aB�a�CO��SU�_f��򄺾@�p6��E����l�h�@��[%#�0s �V�CQ�ע�(O���0�{����Abg>c3�z���%�&[dGiV��\A��s9!$�gWX51	4;#�X߲-CA�6�9�X��
]ȈY�����QO�IV73��2y��}��hm��k
�s@�&��a`FTB�N���݀#��\����ᐌbB.���ΐ�fH�(�n��HZ�W��Z�5B��[�J
��^X���UW,�"�5&h���V��IV���vN8 !-�OI u  ��Eِ����o���ҝ��Bjg��\w3�zҭ1�hh����F|*2�)�y���=��hG/E�
x՝���Q:bn #-�����������tm�3�Z�3Qq����-�\����̶2���k�� ��m�PsY�xn���ʛ�mq8�C��kx����\��Y9����iQ�bN�#-����l�R>�]���16�-5�g���\	���%��I�f��-���V��xj*���5&y`$H��/M�1j����oH�"�rױB���Hs��LZꟆ6<����Cס��g�s�4��z��R��1ͭ��z�v�Y&-��(ok˾.��N]A�X�b8��*d;sZ�9%nG���oҺ�=����G���o�>3��o˄�����⢝1E˚�6[���U[��uG�u�uN�Ik�l�0�3��/{��s1���d��T���l��!0�ۤU�q6�~�D����|;��`������O���L�0����cf��cv`��Ǉ��o�7�����������;���m���n�e�%(�l���jᩪ���O���������KJ���P���]5U݌(�Bd��S-�9ՓB�3����ߍL\;W�wԶJ$F]H�@Z��TD����~�S�)��օ0I�.nB��qE���:K��ݨ��q��XTH\�'��+�P�{
��n�J��BqϾ�RSy���ms�dߜ!6�P0[T�jH�]�<�s8񗭦7{���I��U���ن��	M3P/���,C�i��V������*�V-*;V:Ɣx��lۙ��J)id4D'cqJ�\��-�.���$�s���fV�,P�[��ΰ<A @U�}c�e^� o$P뺤!t�b�Eł��<�����C%�=�t��O�Vǝ1�͘�!��d�f�4�2��(�s���v"7�b�}樷�T"RB�,C ��A�8U�i�rn`���ֵ鞩E������i<s0��NXO.J�@��ڐ��H �`�jП{�VyD� �����f�r!���R�̫m�t@��̀�*	#����'{�"�����T�%�|ve�&�3���Qj�0�������G-�y����%b_�Mk�|�dg`�@����&Լq�֖��B�.{���������N�NEW�ޗ�K��)y�ĝ����}�mM37�Jb�)b�P�y�A�!�9+�Q�8��m��&*]�mo�`nxgTk3�$ޘ<�TP;Xl���v�&�D}l��'����b�F�e�/�3�0;�+h���D__�4VO9�vj(R;O��Ȕ�*:|W�PK�A��vo�DM����q�Au7�JUh�i�����k$���)���Obq��0!z�9#Sw�ɩi"��Gi�o��j?���D�4��Y��jS�����~���7���
�~sum+����L5�M5�$,��vZ�m>��s�aH����Θ�/�AtP���Fv��1�p$��S�jᘧ��a:���#��K�������8r����4��i`5 #�0�QW~�z"O�5�������/
����V��S����BqjS�A�J�K�覯BȌfƛo� _xC/O�J�3��D�=Sa7�*�U	�dz�|~��I6���l�2]�?'�K'Ħ�܇?%�����c�_��Iq2]� +�sg�Z����#;Z���(6�M.�&׻�2ۈ�3��T��{<���6���p5:�ҋJ�T�8C*�O6�5�<gĎfڙS$
;@8�����ut��r[��I����4\&:��	�қ��w7�)JnX�	���Ci���^H����`�5������hP8���
w����>igXF�ՆfM8�Z�������Ò�*o�\����q׷�rzv�;o��{D��L�LMѴ_�@J�����N�V��.�L <<�i���23ǽcvS��Gz$�3�hV;B����g��\f����3mC�-����x�N�t0��|�E��lmf^����z�rh���Y����fP��8��q��Vd�|#}���>��v`{+�D63�l֐��Z�G7�,�`�(�r6�ޔpϿ]���6-v��99�� ܩ��2ֻ�L��f
)��O�Y�?�]�r�*hV�Ӯ���T�)O�rc�Uw&v�KB/��M��@��N�B��*��$Y�J���٩��J���,�"wF�6�K±P�� -���M���*�˸�Y[�Т�8�Sl+�7��]����[��5%�)�\�C�w�j8F���-Q���Mf7�������;�
5�e�`��$�x��@4vh���s���t`$8����Nb�.=>��o�G�8��e'H��_*F�3j�*������wT~��>����Zxi�Fr�bƒ<sg�ޙ��R[s¡��U�m��d>�n�_-�gv����G����BS4��8��G��O�)o�:�F�H�Y��Ĕ)~W7ݴw���L������CS4�1��ҾHܳ��>J��Gr���f[���ݻ����uh:�P��P������5J;tm�G�A�µ&��\+�n]4��=���ʗu�.��虝3W�L���k=klvJ,T��k��v�"q���ش��z/�jiJ�Mю؞����*K�4I;oZ��>��#xv>�_x�|�~��5��3��'g	�v���\l$]��|�ӽ��Z�/��-����Y@G5�C��H��I�:u&�x9Iƴ�If���e�"Z������y@ȴ������#H�#n�У���r�{��-���K"�YBt�" �zE��:Gp�	�I%���/V�#v�oW��g���T=w�J�P��q�`�g��]��B�Ҋc���gֻ�����/��dI���q�]	|�= ���d��m~���l��0��h~BFQ��٢
w��NJK�ug�����C��sy�Gm����C�ԗ�R+�&�xy��i���^�`I:�,�y�]�����'��4�2�_��E�;Y��=�Ϋ�F�ߒ���N��5�
5D^S�
����@:�)K��/n��wN}��Ɂ%iԲX��*�Vc#��]UϏ���ǘo��n��6��r拖|���������У��ûo6��7���"p����Cdg8	R޽���q�7`i�k]��BM��,�+��u��D��a�=��`��
���.�����˗WzՄ?Uߛ�?��}�������ߔ���|���s��k�xmϿ�ni�j���?�c�i�й��^�_����0Kb^�ޣ3��1���M�|_�k2��il�UMHT��~�#��l�i[Dz&6|�H;�0v�k]}e.�Ȃ]s^k�k�~�%(~Yc�MX:�p�QN�>�t�{��t��	u�5�﫷Lh�\U�#;�l��\�,��v�^����z�H!�?�Kl��3�+�Ծ��ꫯ��?���      �     x�mOAj1<g_�{q�dɖ� �ز{H�$�?u�CC)!1�L�Z�"���m�P8�P}0Q��-G6Le�yÚbN�Jf������a�-B�<Y$r@z�x�8�1��񹅌� ���s����SŜ���S�AK��E)TE
��S4�e>�,^��3�
��hD�V�W�nUZ-Sک�d�ɡFB�Uc�u!�'�#�L/��"嵴��~�l�۶����l���&����_�����z^���<�e��i�     