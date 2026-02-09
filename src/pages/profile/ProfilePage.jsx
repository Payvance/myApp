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
 * Neha Tembhe           1.0.0         08/01/2026   Profile page component created
 *
 **/

import { useState } from "react";
import { useSelector } from "react-redux";
import VendorPersonalInfo from "./vendor/VendorPersonalInfo";
import CAPersonalInfo from "./ca/CAPersonalInfo";
import BankDetails from "./common/BankDetails";
import AddressDetails from "./common/AddressDetails";
import Button from "../../components/common/button/Button";
import { profileServices } from "../../services/apiService";
import { toast } from "react-toastify";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import PopUp from "../../components/common/popups/PopUp";
import { authServices } from "../../services/apiService";

const ProfilePage = () => {
  const userId = useSelector((state) => state.auth.userId);
  const navigate = useNavigate();

  /* State variables for the profile page */
  const [showSuccessPopup, setShowSuccessPopup] = useState(false);
  const [vendorData, setVendorData] = useState({});
  const [caData, setCaData] = useState({});
  const [bankData, setBankData] = useState({});
  const [addressData, setAddressData] = useState({});
  const [showCA, setShowCA] = useState(false);
  const [roles, setRoles] = useState([]);
  const [selectedRoleId, setSelectedRoleId] = useState(null);

  /* Getting roles for the dropdown and passed this role to the payload while checkbox clicking */
  useEffect(() => {
  const fetchRoles = async () => {
    try {
      const response = await authServices.getRoles();
      const rolesData = response?.data || [];
      setRoles(rolesData);
      const vendorRole = rolesData.find(
        (role) => role.value === "Vendor"
      );
      if (vendorRole) {
        setSelectedRoleId(vendorRole.code); 
      }
    } catch (error) {
      toast.error("Failed to fetch roles");
    }
  };
  fetchRoles();
}, []);


  /* his function is used for the caling the api for the insertion of ca and vendor profile data 
    Sends the status as inactive while data is sends
  */
  const handleSubmit = async (e) => {
    e.preventDefault();

    let payload = {
      userId,
      roleId: selectedRoleId,
      status: "pending_approval",
   
      /* Vendor */
      name: vendorData.businessName || "",
      email: vendorData.email || "",
      phone: vendorData.mobile || "",
      vendorType: vendorData.vendorType || "",
      experienceYears: vendorData.yearsOfExperience || "",
      gstNo: vendorData.gstNo || "",
      cinNo: vendorData.cinNo || "",
      panNo: vendorData.panNo || "",
      tanNo: vendorData.tanNo || "",
      aadharNo: vendorData.aadhaarNo || "",

      /* Address */
      address: {
        houseBuildingNo: addressData.houseNo || "",
        houseBuildingName: addressData.buildingName || "",
        roadAreaPlace: addressData.area || "",
        landmark: addressData.landmark || "",
        village: addressData.village || "",
        taluka: addressData.taluka || "",
        city: addressData.city || "",
        district: addressData.district || "",
        state: addressData.state || "",
        pincode: addressData.pincode || "",
        postOffice: addressData.postOffice || "",
        country: addressData.country || "",
      },

      /* Bank */
      bank: {
        bankName: bankData.bankName || "",
        branchName: bankData.branchName || "",
        accountNumber: bankData.accountNumber || "",
        ifscCode: bankData.ifscCode || "",
      },
    };

    /* Append CA details only if toggle enabled */
    if (showCA) {
      payload = {
        ...payload,
          name: caData.name || "",
          email: caData.email || "",
          phone: caData.phone || "",
          caRegNo: caData.caRegNo || "",
          enrollmentYear: caData.enrollmentYear || "",
          icaiMemberStatus: caData.icaiMemberStatus || "",
          practiceType: caData.practiceType || "",
          firmName: caData.firmName || "",
          icaiMemberNo: caData.icaiMemberNo || "",
          aadharNo: caData.aadharNo || "",
          caType: vendorData.vendorType || "",
      };
    }

    try {
      const res = await profileServices.createOrUpdateProfile(payload);
      if (res?.data?.success === false) {
        toast.error(res.data.message || "Profile submission failed");
        return;
      }
      // Show success popup instead of toast
      setShowSuccessPopup(true);
    } catch (error) {
      toast.error("Failed to submit profile data");
    }
  };

  const handleLogout = () => {
  navigate("/signin");
};
  return (
    <div className="form-page">
      <form className="form-card" onSubmit={handleSubmit}>

        {/* Header with gradient title and subtitle */}
        <h1 className="form-heading">Profile Details</h1>
        <span className="form-subtitle">Complete your profile information</span>
        <div className="gradient-line"></div>
        <div className="btn-logout">
          <Button text="Logout" variant="primary" onClick={handleLogout} />
        </div>
        {/* Vendor Section - Single card, just a title */}
        <div className="form-section">
          <VendorPersonalInfo
            vendorData={vendorData}
            setVendorData={setVendorData}
          />
        </div>

        {/* Address Section */}
        <div className="form-section">
          <AddressDetails
            addressData={addressData}
            setAddressData={setAddressData}
          />
        </div>

        {/* Bank Section */}
        <div className="form-section">
          <BankDetails
            bankData={bankData}
            setBankData={setBankData}
          />
        </div>

        {/* CA Toggle Section */}
        <div className="ca-checkbox-box">
          <label className="ca-checkbox-label">
            <input
              type="checkbox"
              checked={showCA}
              onChange={(e) => {
                const isChecked = e.target.checked;
                setShowCA(isChecked);
                const role = roles.find((r) =>
                  isChecked ? r.value === "Chartered Accountant" : r.value === "Vendor"
                );
                if (role) {
                  setSelectedRoleId(role.code);
                }
              }}
            />
            <span className="ca-text">Is Chartered Accountant</span>
          </label>
        </div>

        {/* CA Section (conditionally shown) */}
        {showCA && (
          <div className="form-section">
            <CAPersonalInfo
              caData={caData}
              setCaData={setCaData}
            />
          </div>
        )}

        {/* Submit Button */}
        <div className="profile-footer">
          <Button type="submit" text="Submit" />
        </div>
      </form>

      {/* Success Popup */}
      <PopUp
        isOpen={showSuccessPopup}
        onClose={() => {
          setShowSuccessPopup(false);
        }}
        showCloseButton={true}
        size="small"
      >
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px', alignItems: 'center', textAlign: 'center' }}>
          <div style={{ maxWidth: '350px' }}>
      <p style={{ fontSize: '16px' }}>
        Your profile has been submitted for verification. 
        You will receive an email notification once approved.
      </p>
    </div>
          <div style={{ width: '100px' }}>
            <Button
              text="OK"
              onClick={handleLogout} // Redirect to dashboard or appropriate page
              variant="primary"
            />
          </div>
        </div>
      </PopUp>
    </div>
  );
};

export default ProfilePage;
