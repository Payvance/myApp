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
import { formConfig } from "../../../config/formConfig";
import { vendorDiscountServices } from "../../../services/apiService";

const VendorPersonalInfo = ({ vendorData, setVendorData, disabled, role  }) => {

  useEffect(() => {
  console.log("VendorPersonalInfo updated props:", { vendorData, disabled, role });
}, [vendorData, disabled, role]);

  const [discountOptions, setDiscountOptions] = useState([]);
  
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
}, [vendorData.vendorId]);

 const fetchDiscount = async () => {
  if (!vendorData.vendorId) return;

  try {
    const response = await vendorDiscountServices.getAllDiscounts();
    const discounts = response.data || [];

    // Map API response to OptionInputBox format
    const mappedOptions = discounts.map((discount) => ({
  value: discount.id || "N/A", // what user sees
  label: discount.name,             // what is stored in payload
}));
console.log(mappedOptions)
setDiscountOptions(mappedOptions);
  } catch (error) {
    console.error("Failed to fetch discounts", error);
    setDiscountOptions([]);
  }
};



  return (
    <div className="profile-section">
      <div className="profile-section-title">Basic Details</div>

      {/* Row 1 */}
      <div className="form-row-4">
        {/* Fixed OptionInputBox with proper onChange handler */}
        <OptionInputBox
          label={formConfig.vendorprofile.vendortype.label}
          name="vendorType"
          value={vendorData.vendorType || ""}
          onChange={handleOptionChange}
          options={[
            { label: "Individual", value: "Individual" },
            { label: "Proprietor", value: "Proprietor" },
            { label: "Partnership", value: "Partnership" },
            { label: "Company", value: "Company" },
          ]}
          required
          disabled={disabled}
        />
        <InputField
          label={formConfig.vendorprofile.gstno.label}
          name="gstNo"
          value={vendorData.gstNo || ""}
          onChange={(e) => handleChange("gstNo", e.target.value)}
          validationType="GST"
          required
          max={15}
          disabled={disabled}
        />
         <InputField
          label={formConfig.vendorprofile.cinno.label}
          name="cinNo"
          value={vendorData.cinNo || ""}
          onChange={(e) => handleChange("cinNo", e.target.value)}
          validationType="CIN"
          required
          max={21}
          disabled={disabled}
        />
        <InputField
          label={formConfig.vendorprofile.panno.label}
          name="panNo"
          value={vendorData.panNo || ""}
          onChange={(e) => handleChange("panNo", e.target.value)}
          validationType="PAN"
          required
          max={10}
          disabled={disabled}
        />
      </div>

      {/* Row 2 */}
      <div className="form-row-4">
        
        <InputField
          label={formConfig.vendorprofile.tanno.label}
          name="tanNo"
          value={vendorData.tanNo || ""}
          onChange={(e) => handleChange("tanNo", e.target.value)}
          validationType="TAN"
          required
          max={10}
          min={10}
          disabled={disabled}
        />
        {role === "VENDOR" && (
          <OptionInputBox
            label={formConfig.vendorprofile.assignDiscount.label}
            name="vendorDiscountId"
            value={vendorData.vendorDiscountId || ""} // store id
            onChange={handleDiscountChange} // ✅ new handler
            options={discountOptions} // [{label: 7, value: 7}, ...]
            required
            disabled={disabled}
          />
        )}

      </div>
    </div>
  );
};

export default VendorPersonalInfo;