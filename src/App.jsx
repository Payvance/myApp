import './App.css'
// React Router components for client-side routing
import { BrowserRouter, Routes, Route, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useEffect } from 'react';

// Protected Route component
import ProtectedRoute from './components/ProtectedRoute';
import { getAccessToken } from './services/authService';

// Sign In page component
import SignIn from './pages/auth/login/signin/SignIn.jsx'
// Dashboard component
import Dashboard from './pages/superadmin/dashboard/Dashboard.jsx'
// Vendor Dashboard component
import VendorDashboard from './pages/vendor/dashboard/VendorDashboard.jsx'
// Subscription Plan component
import SubscriptionPlan from './pages/superadmin/subscriptionplan/SubscriptionPlan.jsx'
import ProfilePage from './pages/profile/ProfilePage.jsx';
import PartnerSignup from './pages/auth/partner/PartnerSignup.jsx';
// License Inventory component
// import LicenseInventory from './pages/vendor/licenseInventory/LicenseInventory.jsx'
// Assigned Clients component
import AssignedClients from './pages/vendor/assignedclients/AssignedClients.jsx'

// Add-on Plan component
import AddonPlan from './pages/superadmin/subscriptionplan/AddonPlan.jsx'

// Vendor Discount component
import VendorDiscount from './pages/superadmin/discount&offers/VendorDiscount.jsx'

// Offer Management component
import OfferManagement from './pages/superadmin/discount&offers/OfferManagement.jsx'

// Referral Configuration component
import RefferalConfig from './pages/superadmin/discount&offers/RefferalConfig.jsx'
import CADashboard from './pages/ca/dashboard/CADashboard.jsx';
import RedemptionPending from './pages/ca/redemptionpending/RedemptionPending.jsx';
import Redemption from './pages/ca/redemption/Redemption.jsx';
import UserManagement from './pages/usermanagement/UserManagement.jsx';
import LicenseInventory from './pages/vendor/licenseInventory/LicenseInventory.jsx'
import UserPending from './pages/usermanagement/userpending.jsx';

// Audit component
import Audit from './pages/audit/Audit.jsx'
import AuditView from './pages/audit/auditview/AuditView.jsx'

import TenantDashboard from './pages/tenant/dashboard/TenantDashboard.jsx';


import TenantPlans from './pages/tenant/tenantplans/TenantPlans.jsx';

import UserProfile from './pages/profile/userprofile/UserProfile.jsx';
// Approvals component (removed replaced by LicenseInventory)
// import Approvals from './pages/superadmin/approvals/VendorBatchApprovals.jsx'
// CA Redemption Approvals component
import CaRedamptionaApprovals from './pages/superadmin/approvals/CaRedamptionaApprovals.jsx'
import TenantUserManagement from './pages/tenant/usermanagement/TenantUserManagement.jsx';
import CAManagement from './pages/tenant/camanagement/CAManagement.jsx';
import TenantRequests from './pages/ca/tenantrequests/TenantRequests.jsx';
import TanentPlansManagement from './pages/tenant/plansmanagement/TanentPlansManagement.jsx';
import ForgotPassword from './pages/auth/forgotpassword/ForgotPassword.jsx';
import ResetPassword from './pages/auth/forgotpassword/resetpassword/ResetPassword.jsx';
import BuyPlan from './pages/tenant/tenantplans/BuyPlan.jsx';
import User from './pages/tenant/usermanagement/user/User.jsx';
import RejectUsers from './pages/usermanagement/RejectUsers.jsx';





/**
 * Global authentication checker component
 */
const GlobalAuthChecker = () => {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const checkGlobalAuth = () => {
      const token = getAccessToken();
      const publicRoutes = ['/signin', '/partnerwithus', '/forgotpassword', '/resetpassword'];
      
      // If not authenticated and trying to access protected route
      if (!token && !publicRoutes.includes(location.pathname)) {
        navigate('/signin', { 
          state: { from: location.pathname },
          replace: true 
        });
      }
    };

    checkGlobalAuth();
  }, [navigate, location.pathname]);

  return null; // This component doesn't render anything
};

/**
 * Main application component
 * Handles client-side routing using React Router
 */
function App() {
  // Get base path from environment or default to '/'
  const basePath = import.meta.env.VITE_BASE_PATH || '/';

  return (
    // BrowserRouter enables routing using browser with dynamic base path
    <BrowserRouter basename={basePath}>
      {/* Global auth checker */}
      <GlobalAuthChecker />
      
      {/* Toast container (add only once in whole app) */}
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        closeOnClick
        pauseOnHover
        draggable
        theme="light"
        newestOnTop
      />
      <Routes>
        {/* Sign In page route - Public */}
        <Route path="/signin" element={<SignIn />} />
        {/* Partner Signup - Public */}
        <Route path="/partnerwithus" element={<PartnerSignup />} />
        {/* Forget Password - Public */}
        <Route path="/forgotpassword" element={<ForgotPassword />} />
        {/* Reset Password - Public */}
        <Route path="/resetpassword" element={<ResetPassword />} />
        
        {/* Redirects base URL (/) to /signin */}
        <Route path="/" element={<Navigate to="/signin" replace />} />

        {/* PROTECTED ROUTES - Require Authentication */}
        
        {/* Dashboard route */}
        <Route path="/dashboard" element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        } />
        
        {/* Subscription Plan route */}
        <Route path="/subscriptionplan" element={
          <ProtectedRoute>
            <SubscriptionPlan />
          </ProtectedRoute>
        } />
        
        {/* Vendor Discount route */}
        <Route path="/vendordiscount" element={
          <ProtectedRoute>
            <VendorDiscount />
          </ProtectedRoute>
        } />

        {/* Offer Management route */}
        <Route path="/offermanagement" element={
          <ProtectedRoute>
            <OfferManagement />
          </ProtectedRoute>
        } />

        {/* Referral Configuration route */}
        <Route path="/referralconfiguration" element={
          <ProtectedRoute>
            <RefferalConfig />
          </ProtectedRoute>
        } />
        
        {/* User Management routes */}
        <Route path="/users" element={
          <ProtectedRoute>
            <UserManagement />
          </ProtectedRoute>
        } />
        <Route path="/users/pending" element={
          <ProtectedRoute>
            <UserPending />
          </ProtectedRoute>
        } />
        <Route path="/users/pending/:id/edit" element={
          <ProtectedRoute>
            <UserProfile />
          </ProtectedRoute>
        } />
        <Route path="/users/:id/edit" element={
          <ProtectedRoute>
            <UserProfile />
          </ProtectedRoute>
        } />
        <Route path="/users/:id/view" element={
          <ProtectedRoute>
            <UserProfile />
          </ProtectedRoute>
        } />
        {/* User Management Reject User  */}
        <Route path="/users/reject" element={
          <ProtectedRoute>
            <RejectUsers />
          </ProtectedRoute>
        } />
        <Route path="/users/reject/:id/view" element={
          <ProtectedRoute>
            <UserProfile />
          </ProtectedRoute>
        } />
        
        {/* Profile route */}
        <Route path="/profile" element={
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        } />
        
        {/* License Inventory routes */}
        <Route path="/licenseinventory" element={
          <ProtectedRoute>
            <LicenseInventory />
          </ProtectedRoute>
        } />
        <Route path="/vendor/license-batches/:id/edit" element={
          <ProtectedRoute>
            <LicenseInventory />
          </ProtectedRoute>
        } />
        <Route path="/vendor/license-batches/:id/view" element={
          <ProtectedRoute>
            <LicenseInventory />
          </ProtectedRoute>
        } />

        {/* Vendor Dashboard route */}
        <Route path="/vendordashboard" element={
          <ProtectedRoute>
            <VendorDashboard />
          </ProtectedRoute>
        } />
        
        {/* Assigned Clients route */}
        <Route path="/assignedclients" element={
          <ProtectedRoute>
            <AssignedClients />
          </ProtectedRoute>
        } />
        
        {/* Add-on Plans Route */}
        <Route path="/addonplans" element={
          <ProtectedRoute>
            <AddonPlan />
          </ProtectedRoute>
        } />
        
        {/* Audit routes */}
        <Route path="/audits" element={
          <ProtectedRoute>
            <Audit />
          </ProtectedRoute>
        } />
        <Route path="/audits/:id/view" element={
          <ProtectedRoute>
            <AuditView />
          </ProtectedRoute>
        } />
        
        {/* Approvals route redirected to licenseinventory */}
        <Route path="/approvals" element={
          <ProtectedRoute>
            <Navigate to="/licenseinventory" replace />
          </ProtectedRoute>
        } />
        
        {/* CA Redemption Approvals route */}
        <Route path="/ca-redemption-approvals" element={
          <ProtectedRoute>
            <CaRedamptionaApprovals />
          </ProtectedRoute>
        } />

        {/* CA Dashboard route */}
        <Route path="/cadashboard" element={
          <ProtectedRoute>
            <CADashboard />
          </ProtectedRoute>
        } />
        
        {/* Redemption routes */}
        <Route path="/redemption/pending" element={
          <ProtectedRoute>
            <RedemptionPending />
          </ProtectedRoute>
        } />
        <Route path="/redemption" element={
          <ProtectedRoute>
            <Redemption />
          </ProtectedRoute>
        } />
        
        {/* Tenant Dashboard route */}
        <Route path="/tenantdashboard" element={
          <ProtectedRoute>
            <TenantDashboard />
          </ProtectedRoute>
        } />

        {/* Tenant Management routes */}
        <Route path="/usermanagement" element={
          <ProtectedRoute>
            <TenantUserManagement />
          </ProtectedRoute>
        } />
        <Route path="/camanagement" element={
          <ProtectedRoute>
            <CAManagement />
          </ProtectedRoute>
        } />
        <Route path="/tenantrequests" element={
          <ProtectedRoute>
            <TenantRequests />
          </ProtectedRoute>
        } />
        <Route path="/plansmanagement" element={
          <ProtectedRoute>
            <TanentPlansManagement />
          </ProtectedRoute>
        } />
        <Route path="/tenantplanss" element={
          <ProtectedRoute>
            <TenantPlans />
          </ProtectedRoute>
        } />
        <Route path="/BuyPlan" element={
          <ProtectedRoute>
            <BuyPlan/>
          </ProtectedRoute>
        } />
        
        {/* Tenant User Creation routes */}
        <Route path="/usermanagement/:id/view" element={
          <ProtectedRoute>
            <User />
          </ProtectedRoute>
        } />
        <Route path="/usermanagement/:id/edit" element={
          <ProtectedRoute>
            <User />
          </ProtectedRoute>
        } />
      </Routes>
    </BrowserRouter>
  )

}

export default App
