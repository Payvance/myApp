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
 * Neha Tembhe           1.0.0         08/01/2026   vendor profile component created
 *
 **/

import { useEffect, useState } from "react";
import InputField from "../../../components/common/inputfield/InputField";
import OptionInputBox from "../../../components/common/optioninputbox/OptionInputBox";
import Checkbox from "../../../components/common/checkbox/Checkbox";
import DocumentInputField from "../../../components/common/inputfield/DocumentInputField";
import { formConfig } from "../../../config/formConfig";
import { vendorDiscountServices } from "../../../services/apiService";
import { toast } from "react-toastify";

const VendorPersonalInfo = ({ vendorData, setVendorData, disabled, role, validationErrors, setValidationErrors, canEditDiscount }) => {

  useEffect(() => {
    // console.log("VendorPersonalInfo updated props:", { vendorData, disabled, role, canEditDiscount });
  }, [vendorData, disabled, role, canEditDiscount]);

  const [discountOptions, setDiscountOptions] = useState([]);
  // const [validationErrors, setValidationErrors] = useState({});

  const handleChange = (name, value) => {
    if (name === "tanNo") {
      // Allow typing, but cap at 10 characters
      if (value.length > 10) return;
    }

    if (name === "vendorType" && value && value.target) {
      setVendorData((prev) => ({ ...prev, [name]: value.target.value }));
    } else {
      setVendorData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleFileChange = (e, fieldName) => {
    const file = e.target.files?.[0];

    // Handle clearing document
    if (!file) {
      setVendorData((prev) => {
        const newData = { ...prev, [fieldName]: null };
        // If MSME document is cleared, hide the conditional field by unchecking MSME Registered
        if (fieldName === "msmeDocument") {
          newData.msmeRegister = false;
        }
        return newData;
      });
      return;
    }

    // File size restriction: 2MB
    const maxSize = 2 * 1024 * 1024;
    if (file.size > maxSize) {
      toast.error("File size exceeds 2MB limit.");
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const base64String = reader.result.split(",")[1];
      setVendorData((prev) => ({ ...prev, [fieldName]: base64String }));
    };
    reader.readAsDataURL(file);
  };

  const handleCheckboxChange = (e) => {
    const { name, checked } = e.target;
    setVendorData((prev) => {
      const newData = { ...prev, [name]: checked };
      // If MSME Registered is unchecked, also clear the MSME document
      if (name === "msmeRegister" && !checked) {
        newData.msmeDocument = null;
      }
      return newData;
    });
  };

  /* Alternatively, you can fix the OptionInputBox onChange handler directly */
  const handleOptionChange = (value) => {
    const actualValue = value?.target?.value ? value.target.value : value;
    setVendorData((prev) => ({ ...prev, vendorType: actualValue }));
  };

  const handleDiscountChange = (value) => {
    // If OptionInputBox returns value directly
    const selectedValue = value?.target?.value ?? value; // fallback
    setVendorData((prev) => ({ ...prev, vendorDiscountId: selectedValue }));
  };

  useEffect(() => {
    fetchDiscount();
  }, [role]);

  const fetchDiscount = async () => {
    if (role !== "VENDOR") return;

    try {
      const response = await vendorDiscountServices.getAllDiscounts();
      const discounts = response.data?.content || []; // Extract content from pagination

      // Map API response to OptionInputBox format
      const mappedOptions = discounts.map((discount) => ({
        code: discount.id,      // what is stored (ID)
        value: discount.name,   // what user sees (Name)
      }));
      setDiscountOptions(mappedOptions);
    } catch (error) {
      console.error("Failed to fetch discounts", error);
      setDiscountOptions([]);
    }
  };

  return (
    <>
      <div className="profile-section">
        <div className="profile-section-title">Company Information</div>

        {/* Row 1 */}
        <div className="form-row-4">
          {/* Fixed OptionInputBox with proper onChange handler */}
          <InputField
            label={formConfig.vendorprofile.companyname.label}
            name="companyName"
            value={vendorData.companyName || ""}
            onChange={(e) => handleChange("companyName", e.target.value)}
            required
            validationErrors={validationErrors || {}}
            setValidationErrors={setValidationErrors}
            disabled={disabled}
          />
          <OptionInputBox
            label={formConfig.vendorprofile.vendortype.label}
            name="vendorType"
            value={vendorData.vendorType || ""}
            onChange={handleOptionChange}
            options={[
              { code: "Individual", value: "Individual" },
              { code: "Proprietor", value: "Proprietor" },
              { code: "Partnership", value: "Partnership" },
              { code: "Company", value: "Company" },
            ]}
            required
            validationErrors={validationErrors || {}}
            setValidationErrors={setValidationErrors}
            disabled={disabled}
          />

          <InputField
            label={formConfig.vendorprofile.email.label}
            name="email"
            value={vendorData.email || ""}
            onChange={(e) => handleChange("email", e.target.value)}
            validationType="EMAIL"
            required
            validationErrors={validationErrors || {}}
            setValidationErrors={setValidationErrors}
            disabled={disabled}
          />
          <InputField
            label={formConfig.vendorprofile.mobile.label}
            name="mobile"
            value={vendorData.mobile || ""}
            onChange={(e) => handleChange("mobile", e.target.value)}
            validationType="MOBILE"
            required
            validationErrors={validationErrors || {}}
            setValidationErrors={setValidationErrors}
            disabled={disabled}
          />
          {role === "VENDOR" && (
            <OptionInputBox
              label={formConfig.vendorprofile.assignDiscount.label}
              name="vendorDiscountId"
              value={vendorData.vendorDiscountId || ""} // store id
              onChange={handleDiscountChange} // new handler
              options={discountOptions} // [{label: 7, value: 7}, ...]
              required
              validationErrors={validationErrors || {}}
              setValidationErrors={setValidationErrors}
              disabled={!canEditDiscount}
            />
          )}
        </div>
      </div>

      {/* Legal Documents Section */}
      <div className="profile-section">
        <div className="profile-section-title">Legal Documents</div>

        {/* Legal Documents Row 1 */}
        <div className="form-row-4">
          <InputField
            label={formConfig.vendorprofile.panno.label}
            name="panNo"
            value={vendorData.panNo || ""}
            onChange={(e) => handleChange("panNo", e.target.value)}
            validationType="PAN"
            validationErrors={validationErrors || {}}
            setValidationErrors={setValidationErrors}
            disabled={disabled}
          />
          <DocumentInputField
            label={formConfig.vendorprofile.panDocument.label}
            name="panDocument"
            onChange={(e) => handleFileChange(e, "panDocument")}
            value={vendorData.panDocument}
            disabled={disabled}
            validationErrors={validationErrors}
          />
          <InputField
            label={formConfig.vendorprofile.gstno.label}
            name="gstNo"
            value={vendorData.gstNo || ""}
            onChange={(e) => handleChange("gstNo", e.target.value)}
            validationType="GST"
            validationErrors={validationErrors || {}}
            setValidationErrors={setValidationErrors}
            disabled={disabled}
          />
          <DocumentInputField
            label={formConfig.vendorprofile.gstDocument.label}
            name="gstDocument"
            onChange={(e) => handleFileChange(e, "gstDocument")}
            value={vendorData.gstDocument}
            disabled={disabled}
            validationErrors={validationErrors}
          />
        </div>

        {/* Legal Documents Row 2 */}
        <div className="form-row-4">
          <div className="checkbox-input-container">
            <label className="floating-label">{formConfig.vendorprofile.msmeRegister.label}</label>
            <Checkbox
              label="Registered Under MSME act"
              checked={vendorData.msmeRegister || false}
              onChange={(e) => handleCheckboxChange({ ...e, target: { ...e.target, name: 'msmeRegister' } })}
              disabled={disabled}
            />
          </div>
          {vendorData.msmeRegister && (
            <DocumentInputField
              label={formConfig.vendorprofile.msmeDocument.label}
              name="msmeDocument"
              onChange={(e) => handleFileChange(e, "msmeDocument")}
              value={vendorData.msmeDocument}
              disabled={disabled}
              validationErrors={validationErrors}
            />
          )}
          <InputField
            label={formConfig.vendorprofile.cinno.label}
            name="cinNo"
            value={vendorData.cinNo || ""}
            onChange={(e) => handleChange("cinNo", e.target.value)}
            validationType="CIN"
            validationErrors={validationErrors || {}}
            setValidationErrors={setValidationErrors}
            disabled={disabled}
          />
        </div>
      </div>
    </>
  );
};

export default VendorPersonalInfo;