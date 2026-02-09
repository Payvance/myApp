import { useEffect, useState } from "react";
import InputField from "../../../components/common/inputfield/InputField";
import formConfig from "../../../config/formConfig";
import useOtp from "../../../hooks/useOtp";
import Button from "../../../components/common/button/Button";
import { useNavigate } from "react-router-dom";
import { authServices } from "../../../services/apiService";
import { toast } from "react-toastify";

/**
 *
 * author                version        date        change description
 * Shreyash Talwekar      1.0.1         22/01/2026  Forgot password page with OTP
 *
 **/

const ForgotPassword = () => {
    const [email, setEmail] = useState("");
    const navigate = useNavigate();
    const [isCheckingEmail, setIsCheckingEmail] = useState(false);
    const {
        otp,
        otpVerified,
        otpLoading,
        showOtpModal,
        resendTimer,
        canResend,
        sendOtp,
        verifyOtp,
        resendOtp,
        cancelOtp,
        handleOtpChange,
        handleOtpKeyDown,
    } = useOtp();

    useEffect(() => {
        if (otpVerified) {
            navigate("/resetpassword", {
                state: { email },
            });
        }
    }, [otpVerified, navigate, email]);
   
    return (
        <div className="signin-wrapper">
            <div
                className="signin-card"
                style={{ width: "420px", gridTemplateColumns: "1fr" }}
            >
                <div className="signup-panel" style={{ position: "static", width: "100%" }}>
                    <div className="signup-inner" style={{ marginLeft: "-20%" }}>
                        {/* HEADER */}
                        <div className="welcome-header">
                            <h2>Forgot Password</h2>
                            <div className="gradient-line"></div>
                            <p>Recover Your Password</p>
                        </div>

                        {/* EMAIL + OTP */}
                        <div className="otp-row">
                            <div className="otp-input-col">
                                <InputField
                                    label={formConfig.signin.email.label}
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    validationType="EMAIL"
                                    disabled={otpVerified}
                                    max={50}
                                />
                            </div>

                            <div className="otp-btn-col">
                                {!otpVerified ? (
                                    <button
                                        className="otp-btn"
                                        onClick={() => sendOtp(email, "email")}
                                        disabled={otpLoading || isCheckingEmail}
                                    >
                                       {otpLoading || isCheckingEmail ? "Processing..." : "Send OTP"}
                                    </button>
                                ) : (
                                    <span className="otp-verified">✔ Verified</span>
                                )}
                            </div>
                        </div>
                        {/* CANCEL BUTTON */}
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

                {/* OTP MODAL */}
                {showOtpModal && (
                    <div className="otp-modal-overlay">
                        <div className="otp-modal">
                            <h3>Verify OTP</h3>
                            <p>Enter the 6-digit OTP sent to</p>
                            <strong>{email}</strong>

                            <div className="otp-box-wrapper">
                                {otp.map((digit, index) => (
                                    <input
                                        key={index}
                                        id={`otp-${index}`}
                                        type="text"
                                        maxLength="1"
                                        value={digit}
                                        onChange={(e) => handleOtpChange(e.target.value, index)}
                                        onKeyDown={(e) => handleOtpKeyDown(e, index)}
                                        className="otp-box"
                                    />
                                ))}
                            </div>

                            <div className="otp-actions">
                                <Button
                                    text="Verify OTP"
                                    onClick={() => verifyOtp(email, "email")}
                                    disabled={otpLoading}
                                />

                                <button
                                    className={`forgot ${!canResend ? "disabled-link" : ""}`}
                                    disabled={!canResend || otpLoading}
                                    onClick={() => resendOtp(email, "email")}
                                >
                                    {canResend
                                        ? "Resend OTP"
                                        : `Resend OTP in ${resendTimer}s`}
                                </button>

                                <button className="forgot" onClick={cancelOtp}>
                                    Cancel
                                </button>
                            </div>
                        </div>
                    </div>
                )}

            </div>


        </div>
    );
};

export default ForgotPassword;
