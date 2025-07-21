import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import type { User, Role } from '@/lib/types';
import { apiClient, LoginResponse } from '@/lib/api-client';

interface AuthState {
  user: User | null;
  role: Role | null;
  isAuthenticated: boolean;
  accessToken: string | null;
  refreshToken: string | null;
  login: (username: string, password: string) => Promise<User | null>;
  logout: () => Promise<void>;
  setUserManually: (user: User) => void;
  refreshAuthToken: () => Promise<boolean>;
  initializeAuth: () => void;
  updateTokenFromApiClient: (newAccessToken: string) => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      role: null,
      isAuthenticated: false,
      accessToken: null,
      refreshToken: null,
      
      login: async (username: string, password: string) => {
        try {
          const response = await apiClient.login(username, password);

          if (!response.success || !response.data) {
            console.error('Login failed:', response.message);
            return null;
          }

          console.log('Full login response:', response);
          console.log('Response success:', response.success);
          console.log('Response data exists:', !!response.data);
          
          // The API client response structure is now { success: true, data: backendResponse }
          // The backend response is { success: true, data: userData, message: string }
          const backendResponse = response.data;
          console.log('Backend response structure:', backendResponse);
          
          // Extract the actual user data from the backend response
          const userData = backendResponse?.data || backendResponse;
          console.log('Login response userData:', userData);
          console.log('userData type:', typeof userData);
          console.log('userData keys:', userData ? Object.keys(userData) : 'none');
          
          // Convert backend user format to frontend user format
          console.log('Raw role from backend:', userData.role, 'type:', typeof userData.role);
          console.log('All userData properties:');
          for (const [key, value] of Object.entries(userData)) {
            console.log(`  ${key}:`, value, `(${typeof value})`);
          }
          
          const user: User = {
            id: userData.id,
            name: userData.name,
            username: userData.username,
            password: '', // Don't store password
            role: userData.role as Role,
            active: userData.active,
            employeeId: userData.employeeId,
            institutionId: userData.institutionId,
            institution: userData.institution,
            // Convert dates to strings for proper serialization
            createdAt: userData.createdAt ? new Date(userData.createdAt) : new Date(),
            updatedAt: userData.updatedAt ? new Date(userData.updatedAt) : new Date(),
          };
          
          console.log('Constructed user object:');
          for (const [key, value] of Object.entries(user)) {
            console.log(`  ${key}:`, value, `(${typeof value})`);
          }
          
          console.log('User role after casting:', user.role, 'type:', typeof user.role);
          
          console.log('Converted user object:', user);

          console.log('Setting auth state with role:', user.role);
          
          // Validate role before setting
          if (!user.role) {
            console.error('Warning: User role is null/undefined, this should not happen');
          }
          
          // Ensure role is properly set - extract it directly to avoid any reference issues
          const userRole = user.role;
          console.log('Setting auth state - user role extracted:', userRole);
          
          set({ 
            user, 
            role: userRole, 
            isAuthenticated: true,
            accessToken: null, // No JWT tokens in session-based auth
            refreshToken: null,
          });
          
          // Verify the state was set correctly
          const newState = get();
          console.log('Auth state after setting:', { 
            userId: newState.user?.id, 
            role: newState.role, 
            isAuthenticated: newState.isAuthenticated 
          });
          
          return user;
        } catch (error) {
          console.error('Login error:', error);
          return null;
        }
      },

      logout: async () => {
        try {
          // Call backend logout endpoint
          await apiClient.logout();
        } catch (error) {
          console.error('Logout error:', error);
        } finally {
          // Clear tokens from API client
          apiClient.clearToken();
          
          // Clear local storage
          if (typeof window !== 'undefined') {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
          }
          
          // Reset store state
          set({ 
            user: null, 
            role: null, 
            isAuthenticated: false,
            accessToken: null,
            refreshToken: null,
          });
        }
      },

      refreshAuthToken: async () => {
        const { refreshToken } = get();
        
        if (!refreshToken) {
          return false;
        }

        try {
          const response = await apiClient.refreshToken(refreshToken);
          
          if (!response.success || !response.data) {
            // Refresh failed, logout user
            get().logout();
            return false;
          }

          const newAccessToken = response.data.accessToken;
          
          // Update API client with new token
          apiClient.setToken(newAccessToken);
          
          // Update localStorage
          if (typeof window !== 'undefined') {
            localStorage.setItem('accessToken', newAccessToken);
          }
          
          // Update store
          set({ accessToken: newAccessToken });
          
          return true;
        } catch (error) {
          console.error('Token refresh error:', error);
          get().logout();
          return false;
        }
      },

      setUserManually: (user: User) => {
        set({ user, role: user.role, isAuthenticated: true });
      },

      initializeAuth: () => {
        // For session-based auth, just check if user data is persisted
        const currentState = get();
        console.log('initializeAuth - current state:', { 
          hasUser: !!currentState.user, 
          role: currentState.role,
          userRole: currentState.user?.role,
          isAuthenticated: currentState.isAuthenticated,
          username: currentState.user?.username
        });
        
        // Clear stale development data
        if (currentState.user?.name === 'System Administrator') {
          console.log('Clearing stale development user data');
          set({ user: null, role: null, isAuthenticated: false });
          return;
        }
        
        // Ensure role consistency after initialization
        if (currentState.user && currentState.user.role && currentState.role !== currentState.user.role) {
          console.log('initializeAuth: Fixing role mismatch', {
            stateRole: currentState.role,
            userRole: currentState.user.role
          });
          set({ role: currentState.user.role });
        }
        
        if (currentState.user && !currentState.isAuthenticated) {
          console.log('Setting isAuthenticated to true');
          set({ isAuthenticated: true });
        }
      },

      // Add a method to sync token updates from API client
      updateTokenFromApiClient: (newAccessToken: string) => {
        set({ accessToken: newAccessToken });
        if (typeof window !== 'undefined') {
          localStorage.setItem('accessToken', newAccessToken);
        }
      },
    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => localStorage),
      // Persist user authentication state including role
      partialize: (state) => {
        console.log('Partializing state for persistence:', {
          hasUser: !!state.user,
          role: state.role,
          userRole: state.user?.role,
          isAuthenticated: state.isAuthenticated,
          username: state.user?.username
        });
        
        // Ensure the user object is properly serializable by converting Dates to ISO strings
        const serializableUser = state.user ? {
          ...state.user,
          createdAt: state.user.createdAt instanceof Date ? state.user.createdAt : new Date(state.user.createdAt),
          updatedAt: state.user.updatedAt instanceof Date ? state.user.updatedAt : new Date(state.user.updatedAt),
        } : null;
        
        const persistedState = {
          user: serializableUser,
          role: state.role,
          isAuthenticated: state.isAuthenticated,
        };
        
        console.log('State being persisted:', persistedState);
        return persistedState;
      },
      onRehydrateStorage: () => {
        return (state, error) => {
          if (error) {
            console.error('Auth store rehydration error:', error);
          } else if (state) {
            console.log('Auth store rehydrated successfully:', {
              hasUser: !!state.user,
              role: state.role,
              userRole: state.user?.role,
              isAuthenticated: state.isAuthenticated,
              userId: state.user?.id,
              username: state.user?.username
            });
            
            // Restore Date objects from strings after rehydration
            if (state.user) {
              if (typeof state.user.createdAt === 'string') {
                state.user.createdAt = new Date(state.user.createdAt);
              }
              if (typeof state.user.updatedAt === 'string') {
                state.user.updatedAt = new Date(state.user.updatedAt);
              }
            }
            
            // Check for role mismatch between state.role and state.user.role
            if (state.user && state.role !== state.user.role) {
              console.warn('ROLE MISMATCH DETECTED:', {
                stateRole: state.role,
                userRole: state.user.role,
                username: state.user.username
              });
              // Fix the role mismatch by using the user's role
              console.log('Fixing role mismatch...');
              state.role = state.user.role;
            }
          }
        };
      },
    }
  )
);
