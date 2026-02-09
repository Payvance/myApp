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
 * Aakanksha              1.0.0        20/1/2026    Personal Details component created.
 *
 **/
import InputField from "../../../components/common/inputfield/InputField";
import { formConfig } from "../../../config/formConfig";

const PersonalDetails = ({ personalData, setPersonalData, disabled, disableEmail = false, disablePhone = false,
 }) => {
    
    const handleChange = (name, value) => {
        setPersonalData(prev => ({ ...prev, [name]: value }));
    };

    return (
        <div className="profile-section">
            <div className="profile-section-title">Personal Details</div>

            {/* Row 1 */}
            <div className="form-row-4">
                <InputField
                    label={formConfig.signin.fullname.label}
                    name="userName"
                    value={personalData.userName || ""}
                    onChange={(e) => handleChange("userName", e.target.value)}
                    validationType="ALPHABETS_AND_SPACE"
                    max={50}
                    required
                    disabled={disabled}
                />

                <InputField
                    label={formConfig.signin.email.label}
                    name="userEmail"
                    value={personalData.userEmail || ""}
                    onChange={(e) => handleChange("userEmail", e.target.value)}
                    validationType="EMAIL"
                    max={50}
                    required
                    disabled={disabled || disableEmail}
                />

                <InputField
                    label={formConfig.signin.mobileno.label}
                    name="userPhone"
                    value={personalData.userPhone || ""}
                    onChange={(e) => handleChange("userPhone", e.target.value)}
                    validationType="NUMBER_ONLY"
                    max={10}
                    required
                    disabled={disabled || disablePhone}
                />
            </div>
        </div>
    );
};

export default PersonalDetails;
