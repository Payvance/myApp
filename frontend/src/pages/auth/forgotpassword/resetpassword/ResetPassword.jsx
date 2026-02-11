import { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import InputField from "../../../../components/common/inputfield/InputField";
import { authServices } from "../../../../services/apiService";
import { toast } from "react-toastify";
import Button from "../../../../components/common/button/Button";
import PaswordInputBox from "../../../../components/common/inputfield/PaswordInputBox";





const ResetPassword = () => {
    const navigate = useNavigate();
    const location = useLocation();

    const email = location.state?.email;

    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [isPasswordValid, setIsPasswordValid] = useState(false);
    /* -------------------- BLOCK DIRECT ACCESS -------------------- */
    useEffect(() => {
        if (!email) {
            toast.error("Unauthorized access");
            navigate("/forgotpassword");
        }
    }, [email, navigate]);
    /* -------------------- RESET PASSWORD -------------------- */
    const handleResetPassword = async () => {
        if (!newPassword || !confirmPassword) {
            toast.error("Please fill all the fields");
            return;
        }
        if (!isPasswordValid) {
            toast.error("Password does not meet the criteria");
            return;
        }
        

        if (newPassword !== confirmPassword) {
            toast.error("Confirm Password does not match Password");
            return;
        }

        try {
            setLoading(true);
            
            // DEMO API CALL
            await authServices.resetPassword({
                email,
                newPassword,
            });
            toast.success("Password reset successfully");
            navigate("/signin");
        } catch (error) {
            toast.error(error?.response?.data || "Failed to reset password");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="signin-wrapper">
            <div
                className="signin-card"
                style={{ width: "420px", gridTemplateColumns: "1fr" }}
            >
                <div className="signup-panel" style={{ position: "static", width: "100%" }}>
                    <div className="signup-inner" style={{ marginLeft: "-20%" }}>
                        <div className="welcome-header">
                            <h2>Reset Password</h2>
                            <div className="gradient-line"></div>
                            <p>Create a new password</p>
                            <div
                                style={{
                                    display: "flex",
                                    justifyContent: "center",
                                    alignItems: "center",
                                    height: "100%",
                                }}
                            >
                                <strong>{email}</strong>
                            </div>
                        </div>
                        <div className="position-relative signup-pass-container">
                        <PaswordInputBox
                            label="New Password"
                            value={newPassword}
                            type={showNewPassword ? "text" : "password"}
                            onChange={setNewPassword}
                            onValidationChange={setIsPasswordValid}
                            showCaseInfo={true}
                        />
                        <i
                            className={`bi ${showNewPassword ? "bi-eye" : "bi-eye-slash"} eye-icon-signup`}
                            onClick={() => setShowNewPassword(!showNewPassword)}
                        > </i>
                        </div>
                        <div className="position-relative signup-pass-container">
                        <InputField
                            label="Confirm Password"
                            type={showConfirmPassword ? "text" : "password"}
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            max={16}
                            onPaste={(e) => e.preventDefault()}
                        />
                        <i
                            className={`bi ${showConfirmPassword ? "bi-eye" : "bi-eye-slash"} eye-icon-signup`}
                            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                        > </i>
                        </div>
                        <Button text="Reset Password" onClick={handleResetPassword} />
                       <div style={{ display: "flex", justifyContent: "center", marginTop: "16px" }}>
  <button
    className="forgot"
    type="button"
    onClick={() => navigate("/signin")}
  >
    Cancel
  </button>
</div>

                    </div>
                </div>
            </div>
        </div>
    );
};

export default ResetPassword;
