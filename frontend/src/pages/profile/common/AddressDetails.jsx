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
 * Neha Tembhe           1.0.0         07/01/2026   Address Details component created.
 *
 **/
import InputField from "../../../components/common/inputfield/InputField";
import { formConfig } from "../../../config/formConfig";
import { externalServices } from "../../../services/apiService";
import { useState } from "react";
import { toast } from "react-toastify";
const AddressDetails = ({ addressData, setAddressData, disabled,validationErrors,setValidationErrors }) => {
const [isFetched, setIsFetched] = useState(false);
// const [validationErrors, setValidationErrors] = useState({});
  /* Handled the changed function for the address details form */
  const handleChange = (name, value) => {
    const trimmedValue =
    typeof value === "string" ? value.trimStart() : value;
    // Update the pincode/field value immediately
    setAddressData(prev => ({ ...prev, [name]: trimmedValue }));

    if (name === "pincode") {
      // Fetch data only when exactly 6 digits are reached
      if (value.length === 6) {
        fetchPincodeData(value);
      }
      // If cleared completely
      else if (value.length < 6) {
        resetAddressFields();
      }
    }
  };

  const handleBlur = (name, value) => {
    if (name === "pincode") {
      // If user leaves field with less than 6 digits (and not empty)
      if (value.length > 0 && value.length < 6) {
        toast.error("Pincode must be 6 digits");
        resetAddressFields();
      }
    }
  };
  // Function to fetch address details based on pincode
  const fetchPincodeData = async (pincode) => {
    try {
      const response = await externalServices.getPostalInfoByPincode(pincode);
      const data = response.data[0];
      if (data.Status === "Error") {
        setTimeout(() => {
          toast.error("Pincode does not exist. Please check and try again.");
        }, 2000);
        resetAddressFields(); // Clear fields and unlock them
        return;
      }
      if (data.Status === "Success") {
        const details = data.PostOffice[0];
        
        // Auto-fill the address fields
        setAddressData(prev => ({
          ...prev,
          city: details.Block || details.Division,
          district: details.District,
          state: details.State,
          country: details.Country,
        }));
        setIsFetched(true);
      }
    } catch (error) {
      console.error("Pincode API Error:", error);
    }
  };
  const resetAddressFields = () => {
    setAddressData(prev => ({
      ...prev,
      city: "",
      district: "",
      state: "",
      country: "",
    }));
    setIsFetched(false);
  };
  return (
    <div className="profile-section">
      <div className="profile-section-title">Address Details</div>

      {/* Row 1 */}
      <div className="form-row-4">
        <InputField
          label={formConfig.address.houseno.label}
          name="houseNo"
          value={addressData.houseNo}
          onChange={(e) => handleChange("houseNo", e.target.value)}
          validationType="HOUSE"
          max={20}
          required
          validationErrors={validationErrors || {}} 
          setValidationErrors={setValidationErrors}
          disabled={disabled}
        />

        <InputField
          label={formConfig.address.housename.label}
          name="buildingName"
          value={addressData.buildingName}
          onChange={(e) => handleChange("buildingName", e.target.value)}
          validationType="HOUSE"
          max={50}
          disabled={disabled}
        />

        <InputField
          label={formConfig.address.roadareaplace.label}
          name="area"
          value={addressData.area}
          onChange={(e) => handleChange("area", e.target.value)}
          validationType="HOUSE"
          max={50}
          required
          validationErrors={validationErrors || {}} 
          setValidationErrors={setValidationErrors}
          disabled={disabled}
        />

        <InputField
          label={formConfig.address.landmark.label}
          name="landmark"
          value={addressData.landmark}
          onChange={(e) => handleChange("landmark", e.target.value)}
          validationType="LANDMARK"
          max={50}
          disabled={disabled}
        />
      </div>

      {/* Row 2 */}
      <div className="form-row-4">

        <InputField
          label={formConfig.address.pincode.label}
          name="pincode"
          value={addressData.pincode}
          onChange={(e) => handleChange("pincode", e.target.value)}
          onBlur={(e) => handleBlur("pincode", e.target.value)}
          validationType="NUMBER_ONLY"
          max={6}
          required
          validationErrors={validationErrors || {}} 
          setValidationErrors={setValidationErrors}
          disabled={disabled}
        />

        <InputField
          label={formConfig.address.city.label}
          name="city"
          value={addressData.city}
          onChange={(e) => handleChange("city", e.target.value)}
          validationType="ALPHABETS_AND_SPACE"
          max={30}
          required
          validationErrors={validationErrors || {}} 
          setValidationErrors={setValidationErrors}
          disabled={disabled}
        />

        <InputField
          label={formConfig.address.village.label}
          name="village"
          value={addressData.village}
          onChange={(e) => handleChange("village", e.target.value)}
          validationType="ALPHABETS_AND_SPACE_HYPHEN"
          max={30}
          disabled={disabled}
        />
        <InputField
          label={formConfig.address.taluka.label}
          name="taluka"
          value={addressData.taluka}
          onChange={(e) => handleChange("taluka", e.target.value)}
          validationType="ALPHABETS_AND_SPACE_HYPHEN"
          max={30}
          disabled={disabled}
        />

      </div>

      {/* Row 3 */}
      <div className="form-row-4">
        <InputField
          label={formConfig.address.district.label}
          name="district"
          value={addressData.district}
          onChange={(e) => handleChange("district", e.target.value)}
          validationType="ALPHABETS_AND_SPACE"
          max={30}
          required
          validationErrors={validationErrors || {}} 
          setValidationErrors={setValidationErrors}
          disabled={disabled}
        />

        <InputField
          label={formConfig.address.state.label}
          name="state"
          value={addressData.state}
          onChange={(e) => handleChange("state", e.target.value)}
          validationType="ALPHABETS_AND_SPACE"
          max={30}
          required
          validationErrors={validationErrors || {}} 
          setValidationErrors={setValidationErrors}
          disabled={disabled}
        />

        <InputField
          label={formConfig.address.postoffice.label}
          name="postOffice"
          value={addressData.postOffice}
          onChange={(e) => handleChange("postOffice", e.target.value)}
          validationType="ALPHABETS_AND_SPACE"
          max={30}
          disabled={disabled}
        />
        <InputField
          label={formConfig.address.country.label}
          name="country"
          value={addressData.country}
          onChange={(e) => handleChange("country", e.target.value)}
          validationType="ALPHABETS_AND_SPACE"
          max={30}
          required
          validationErrors={validationErrors || {}} 
          setValidationErrors={setValidationErrors}
          disabled={disabled}
        />
      </div>
    </div>
  );
};

export default AddressDetails;
