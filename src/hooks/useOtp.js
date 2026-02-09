/**
 * Copyright: © 2025 Payvance Innovation Pvt. Ltd.
 *
 * Organization: Payvance Innovation Pvt. Ltd.
 *
 * This is unpublished, proprietary, confidential source code of Payvance Innovation Pvt. Ltd.
 * Payvance Innovation Pvt. Ltd. retains all title to and intellectual property rights in these materials.
 *
 **/

/**
 *
 * author                version        date        change description
 * Neha Tembhe           1.0.0         10/01/2026   Common function file for otp verification.
 *
 **/

import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { authServices } from "../services/apiService";

const RESEND_TIME = 60;

const useOtp = () => {
  const [otp, setOtp] = useState(Array(6).fill(""));
  const [otpVerified, setOtpVerified] = useState(false);
  const [otpLoading, setOtpLoading] = useState(false);
  const [showOtpModal, setShowOtpModal] = useState(false);

  const [resendTimer, setResendTimer] = useState(0);
  const [canResend, setCanResend] = useState(false);

  /* -------------------- TIMER -------------------- */
  useEffect(() => {
    if (!showOtpModal) return;

    if (resendTimer === 0) {
      setCanResend(true);
      return;
    }

    const timer = setTimeout(() => {
      setResendTimer((prev) => prev - 1);
    }, 1000);

    return () => clearTimeout(timer);
  }, [resendTimer, showOtpModal]);

  /* -------------------- SEND OTP -------------------- */
   const sendOtp = async (identifier, type = "mobile") => {
   
    try {
      setOtpLoading(true);
      if (type === "email") {
        await authServices.emailOtpSend(identifier);
      } else {
        if (!identifier || identifier.length !== 10) {
          toast.error("Enter valid mobile number");
          return;
        }
        await authServices.otpsend(identifier);
      }
      toast.success("OTP sent successfully");
      setShowOtpModal(true);
      setOtp(Array(6).fill(""));
      setResendTimer(RESEND_TIME);
      setCanResend(false);
    } catch (error) {
      // if the email is not registered there will be error message 
    if (type === "email") {
      toast.error("This email is not registered.");
    } 
    else {
      toast.error("Something went wrong. Please try again.");
    }
    } finally {
      setOtpLoading(false);
    }
  };

  /* -------------------- VERIFY OTP -------------------- */
  const verifyOtp = async (identifier, type = "mobile") => {
    const otpValue = otp.join("");

    if (otpValue.length !== 6) {
      toast.error("Enter valid OTP");
      return false;
    }
    if (resendTimer === 0) {
    toast.error("OTP has expired. Please resend the OTP.");
    setOtp(Array(6).fill("")); 
    return false;
    }
    try {
      setOtpLoading(true);
      if (type === "email") {
        await authServices.emailOtpVerify(identifier, otpValue);
      } else {
        await authServices.otpverify({
          mobile: identifier,
          otp: otpValue,
        });
      }

      toast.success("OTP verified successfully");
      setOtpVerified(true);
      setShowOtpModal(false);
      return true;
    } catch (error) {
      toast.error(error?.response?.data || "Invalid OTP");
      setOtp(Array(6).fill("")); // Clears all 6 boxes
      setTimeout(() => {
        document.getElementById("otp-0")?.focus(); // Returns focus to the start
      }, 0);
      return false;
    } finally {
      setOtpLoading(false);
    }
  };

  /* -------------------- RESEND OTP -------------------- */
  const resendOtp = async (identifier, type = "mobile") => {
    try {
      setOtpLoading(true);
      if (type === "email") {
        await authServices.emailOtpSend(identifier);
      } else {
        await authServices.otpsend(identifier);
      }
      toast.success("OTP resent successfully");
      setOtp(Array(6).fill(""));
      setResendTimer(RESEND_TIME);
      setCanResend(false);
      document.getElementById("otp-0")?.focus();
    } catch (error) {
      toast.error(error?.response?.data || "Failed to resend OTP");
    } finally {
      setOtpLoading(false);
    }
  };

  /* -------------------- INPUT HANDLERS -------------------- */
  const handleOtpChange = (value, index) => {
    if (!/^\d?$/.test(value)) return;
    const updatedOtp = [...otp];
    updatedOtp[index] = value;
    setOtp(updatedOtp);
    if (value && index < 5) {
      document.getElementById(`otp-${index + 1}`)?.focus();
    }
  };

  const handleOtpKeyDown = (e, index) => {
    if (e.key === "Backspace" && !otp[index] && index > 0) {
      document.getElementById(`otp-${index - 1}`)?.focus();
    }
  };

   /*-------------------- OTP CANCEL HANDLER --------------------*/
  const cancelOtp = () => {
    setShowOtpModal(false);
    setOtp(Array(6).fill(""));
  };

  return {
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
  };
};

export default useOtp;
