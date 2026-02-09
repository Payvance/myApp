/**
 * Copyright: © 2025 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of
 * Payvance Innovation Pvt. Ltd.
 *
 **/

/**
 *
 * author                version        date        change description
 * Aakanksha              1.0.0        20/1/2026    User Profile component created.
 *
 **/
import React, { useEffect, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import SuperAdminLayout from '../../../layouts/SuperAdminLayout';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import VendorPersonalInfo from '../vendor/VendorPersonalInfo';
import CAPersonalInfo from '../ca/CAPersonalInfo';
import AddressDetails from '../common/AddressDetails';
import BankDetails from '../common/BankDetails';
import PersonalDetails from '../common/PersonalDetails';
import Toggle from '../../../components/common/togglebutton/Toggle';
import Button from '../../../components/common/button/Button';
import { userServices } from '../../../services/apiService';
import '../userprofile/userprofile.css';
import { toast } from "react-toastify";
import PopUp from "../../../components/common/popups/PopUp";
/**
 * UserProfile Component
 */
const UserProfile = () => {
  // Get user ID from URL parameters
  const { id } = useParams();
  const navigate = useNavigate();
  // Get current location to check for "View" mode
  const location = useLocation();
  const isViewMode = location.pathname.includes('/view');
  const isPendingMode = location.pathname.includes('/pending/');

  // State for Reject Popup
  const [showRejectPopup, setShowRejectPopup] = useState(false);
  const [rejectReason, setRejectReason] = useState("");

  /* STATE MANAGEMEN*/

  // Role of the user 
  const [role, setRole] = useState("");

  // Active/Inactive status of the user
  const [isActive, setIsActive] = useState(true);

  // Loading state for API calls
  const [loading, setLoading] = useState(false);

  // Personal Details State (Common for all users)
  const [personalData, setPersonalData] = useState({
    userName: "",
    userEmail: "",
    userPhone: "",
  });

  // Vendor Specific Details State
  const [vendorData, setVendorData] = useState({
    vendorId: "",
    vendorType: "",
    gstNo: "",
    cinNo: "",
    panNo: "",
    tanNo: "",
    vendorDiscountId: ""
  });

  // CA Specific Details State
  const [caData, setCaData] = useState({
    caRegNo: "",
    enrollmentYear: "",
    icaiMemberNo: "",
    firmName: "",
    icaiMemberStatus: "",
    practiceType: "",
    gstNo: "",
    cinNo: "",
    panNo: "",
    tanNo: "",
    caType: "",
  });

  // Address Details State (Common structure)
  const [addressData, setAddressData] = useState({
    houseNo: "",
    buildingName: "",
    area: "",
    landmark: "",
    country: "",
    pincode: "",
    city: "",
    village: "",
    taluka: "",
    district: "",
    state: "",
    postOffice: "",
  });

  // Bank Details State (Common structure)
  const [bankData, setBankData] = useState({
    bankName: "",
    branchName: "",
    accountNumber: "",
    ifscCode: "",
  });



  // Fetch User Details on Component Mount or ID Change
  useEffect(() => {
    const fetchUserDetails = async () => {
      setLoading(true);
      try {
        // API Call to get user details
        const response = await userServices.getUserById(id);
        const data = response.data;

        // Set Common Data
        setRole(data.role);
        setIsActive(data.isActive ?? true);
        mapAddressAndBank(data);
        // Map Personal Data
        setPersonalData({
          userName: data.userName ?? "",
          userEmail: data.userEmail ?? "",
          userPhone: data.userPhone ?? "",
        });

        // Set Role-Specific Data
        if (data.role === "VENDOR") {
          setVendorData({
            vendorId: data.vendorId ?? "",
            vendorType: data.vendorType ?? "",
            gstNo: data.gstNo ?? "",
            cinNo: data.cinNo ?? "",
            panNo: data.panNo ?? "",
            tanNo: data.tanNo ?? "",
            vendorDiscountId: data.vendorDiscountId ?? "",
          });
        }

        if (data.role === "CA") {
  // Mapping CA-prefixed API keys to the state used by the UI
  const caBasicDetails = {
    vendorType: data.caType ?? "", 
    gstNo: data.caGstNo ?? "", 
    cinNo: data.caCinNo ?? "",
    panNo: data.caPanNo ?? "",
    tanNo: data.caTanNo ?? "",
  };

  setVendorData(caBasicDetails);
  setCaData((prev) => ({
    ...prev,
    caRegNo: data.caRegNo ?? "",
    enrollmentYear: data.enrollmentYear ?? "",
    icaiMemberStatus: data.icaiMemberStatus ?? "",
    firmName: data.firmName ?? "",
    practiceType: data.practiceType ?? "",
    icaiMemberNo: data.icaiMemberNo ?? "",
    caType: data.caType ?? "",
    // Keep these in caData as well if needed for other logic
    gstNo: data.caGstNo ?? "",
    cinNo: data.caCinNo ?? "",
    panNo: data.caPanNo ?? "",
    tanNo: data.caTanNo ?? "",
  }));
}
      } catch (error) {
        toast.error("Error fetching profile:", error);
      } finally {
        setLoading(false);
      }
    };

    // Helper function to map Address and Bank details to avoid repetition
    const mapAddressAndBank = (data) => {
      setAddressData({
        houseNo: data.houseBuildingNo ?? "",
        buildingName: data.houseBuildingName ?? "",
        area: data.roadAreaPlace ?? "",
        landmark: data.landmark ?? "",
        country: data.country ?? "",
        pincode: data.pincode ?? "",
        city: data.city ?? "",
        village: data.village ?? "",
        taluka: data.taluka ?? "",
        district: data.district ?? "",
        state: data.state ?? "",
        postOffice: data.postOffice ?? "",
      });

      setBankData({
        bankName: data.bankName ?? "",
        branchName: data.branchName ?? "",
        accountNumber: data.accountNumber ?? "",
        ifscCode: data.ifscCode ?? "",
      });
    };

    fetchUserDetails();
  }, [id]);



  /* EVENT HANDLERS */

  // Handle Approve User
  const handleApprove = async () => {
    try {
      await userServices.approveUser(id, { approve: true });
      toast.success("User Approved successfully!");
      setTimeout(() => navigate("/users/pending"), 2000);
    } catch (error) {
      toast.error("Failed to approve user");
    }
  };

  // Handle Reject User (Submit Reason)
  const handleRejectSubmit = async () => {
    if (!rejectReason.trim()) {
      toast.error("Please provide a reason for rejection");
      return;
    }
    try {
      await userServices.approveUser(id, { approve: false, remark: rejectReason });
      toast.success("User rejected ");
      setTimeout(() => navigate("/users/pending"), 2000);
    } catch (error) {
      toast.error("Failed to reject user");
    }
  };
  // Handle Save Changes 
  const handleSave = async () => {

    try {
      const payload = {
        name: personalData.userName,
        email: personalData.userEmail,
        phone: personalData.userPhone,
        isActive: isActive,
        houseBuildingNo: addressData.houseNo,
        houseBuildingName: addressData.buildingName,
        roadAreaPlace: addressData.area,
        landmark: addressData.landmark,
        village: addressData.village,
        taluka: addressData.taluka,
        city: addressData.city,
        district: addressData.district,
        state: addressData.state,
        pincode: addressData.pincode,
        postOffice: addressData.postOffice,
        country: addressData.country,
        bankName: bankData.bankName,
        branchName: bankData.branchName,
        accountNumber: bankData.accountNumber,
        ifscCode: bankData.ifscCode,

        ...(role === "CA" && {
          caRegNo: caData.caRegNo,
          enrollmentYear: caData.enrollmentYear,
          icaiMemberStatus: caData.icaiMemberStatus,
          practiceType: caData.practiceType,
          firmName: caData.firmName,
          icaiMemberNo: caData.icaiMemberNo,
          gstNo: caData.gstNo,
          cinNo: caData.cinNo,
          panNo: caData.panNo,
          tanNo: caData.tanNo,
          caType: vendorData.vendorType || caData.caType,
        }),

        ...(role === "VENDOR" && {
          vendorName: personalData.userName,
          vendorType: vendorData.vendorType,
          gstNo: vendorData.gstNo,
          cinNo: vendorData.cinNo,
          panNo: vendorData.panNo,
          tanNo: vendorData.tanNo,
          vendorDiscountId: vendorData.vendorDiscountId,
        }),
      };

      //  Call API Service
      const response = await userServices.updateUser(id, payload);

      //  Success Handling (Exactly like handleSignup)
      toast.success("User profile updated successfully!");

      //  Redirection after success
      setTimeout(() => {
        navigate("/users");
      }, 2000);

    } catch (error) {
      toast.error("Update failed:", error);
    }
  };
  /* ==============================
     RENDER UI
     ============================== */
  return (
    <SuperAdminLayout>
      {/* Main Container with Scroll Support */}
      <div className="user-profile-page">
        {/* Page Header*/}
        <div>
          <PageHeader
            title={
              <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                <Button isBack variant="back" text="" />
                {isPendingMode
                  ? "Unapprove User"
                  : isViewMode
                    ? "View User Profile"
                    : "Edit User Profile"}


                <Toggle
                  isOn={isActive}
                  onToggle={() => setIsActive(!isActive)}
                  labelOn="User Status Active"
                  labelOff="User Status Inactive"
                  size="small"
                  disabled={isViewMode || isPendingMode}
                />
              </div>
            }
          />
        </div>

        {loading ? (
          <div>Loading profile...</div>
        ) : (
          <div className="profile-sections-container">

            {/* 1. Personal Details (Common) */}
            <PersonalDetails
              personalData={personalData}
              setPersonalData={setPersonalData}
              disabled={isViewMode || isPendingMode}
            />

            {/* 2. Role Specific Personal Info */}
            {(role === "VENDOR" || role === "CA") && (
              <VendorPersonalInfo
                vendorData={vendorData}
                setVendorData={setVendorData}
                disabled={isViewMode || isPendingMode}
                role={role}
              />
            )}
            {/* 3. Common Address & Bank Details (Rendered for all roles) */}
            {role !== "SUPERADMIN" && (
              <>
                <AddressDetails
                  addressData={addressData}
                  setAddressData={setAddressData}
                  disabled={isViewMode || isPendingMode}
                />
                <BankDetails
                  bankData={bankData}
                  setBankData={setBankData}
                  disabled={isViewMode || isPendingMode}
                />
              </>
            )}
            {role === "CA" && (
              <CAPersonalInfo
                caData={caData}
                setCaData={setCaData}
                disabled={isViewMode || isPendingMode}
              />
            )}


            {/* Save Button (Edit Mode) OR Approve/Reject (Pending Mode) */}
            {!isViewMode && (
              <div className="profile-footer" style={{ display: 'flex', justifyContent: 'flex-end', width: '100%' }}>
                {isPendingMode ? (
                  <div style={{ display: 'flex', gap: '10px' }}>
                    <div style={{ width: 'auto' }}>
                      <Button
                        text="Approve"
                        onClick={handleApprove}
                        disabled={loading}
                        variant="primary"
                      />
                    </div>
                    <div style={{ width: 'auto' }}>
                      <Button
                        text="Reject"
                        onClick={() => setShowRejectPopup(true)}
                        disabled={loading}
                        variant="red"
                      />
                    </div>
                  </div>
                ) : (
                  <div style={{ marginRight: '20px' }}>
                    <Button
                      text="Save Changes"
                      onClick={handleSave}
                      disabled={loading}
                    />
                  </div>
                )}
              </div>
            )}
          </div>
        )}

        {/* Reject Remark Popup */}
        <PopUp
          isOpen={showRejectPopup}
          onClose={() => setShowRejectPopup(false)}
          title="Reject User"
          size="small"
        >
          <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
            <p>Please provide a reason for rejecting this user</p>
            <textarea
              value={rejectReason}
              onChange={(e) => setRejectReason(e.target.value)}
              placeholder="Enter rejection reason..."
              rows={4}
            />
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
              <Button
                text="Cancel"
                onClick={() => {
                  setShowRejectPopup(false);
                  setRejectReason("");
                }}
                variant="red"

              />
              <Button
                text="Submit"
                onClick={handleRejectSubmit}
                disabled={loading}
                variant="secondary"
              />
            </div>
          </div>
        </PopUp>
      </div>
    </SuperAdminLayout >
  );
};

export default UserProfile;