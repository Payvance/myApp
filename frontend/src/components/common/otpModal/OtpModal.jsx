// src/components/common/otpmodal/OtpModal.jsx
import React, { useState, useEffect, useRef } from "react";
import "./OtpModal.css";

const MAX_ATTEMPTS = 3;

const OtpModal = ({
  heading = "Please verify yourself",
  description = "Enter the 6-digit code sent to",
  target,
  otp,               // string[]
  otpLoading = false,
  totalSeconds = 90,
  canResend = false,
  onOtpChange,       // (value, index) => void
  onOtpKeyDown,      // (e, index) => void
  onVerify,          // () => void
  onResend,          // () => void
  onCancel,          // () => void
  verifyError,       // string | null  — pass error message from API
}) => {
  const [secondsLeft, setSecondsLeft]   = useState(totalSeconds);
  const [attempts, setAttempts]         = useState(0);
  const [shaking, setShaking]           = useState(false);
  const [submitState, setSubmitState]   = useState("idle"); // idle | loading | success
  const intervalRef = useRef(null);

  const filled    = otp.filter(Boolean).length;
  const allFilled = filled === otp.length;
  const isExpired = secondsLeft <= 0;
  const isWarning = secondsLeft <= 20 && !isExpired;
  const outOfAttempts = attempts >= MAX_ATTEMPTS;

  /* ── Countdown ── */
  const startTimer = (from) => {
    clearInterval(intervalRef.current);
    setSecondsLeft(from);
    intervalRef.current = setInterval(() => {
      setSecondsLeft(prev => { if (prev <= 1) { clearInterval(intervalRef.current); return 0; } return prev - 1; });
    }, 1000);
  };

  useEffect(() => { startTimer(totalSeconds); return () => clearInterval(intervalRef.current); }, []);

  /* ── React to external verify error ── */
  useEffect(() => {
    if (verifyError) {
      setShaking(true);
      setTimeout(() => setShaking(false), 400);
      setAttempts(a => a + 1);
      setSubmitState("idle");
    }
  }, [verifyError]);

  const handleVerify = () => {
    if (!allFilled || isExpired || outOfAttempts) return;
    setSubmitState("loading");
    onVerify?.();
  };

  const handleResend = () => {
    if (!canResend && !isExpired) return;
    startTimer(totalSeconds);
    setAttempts(0);
    setSubmitState("idle");
    onResend?.();
  };

  const fmt = (s) => {
    if (s <= 0) return "Expired";
    const m = Math.floor(s / 60), sec = s % 60;
    return m > 0 ? (sec > 0 ? `${m} min ${sec} secs left` : `${m} min left`) : `${sec} sec${sec !== 1 ? "s" : ""} left`;
  };

  const progress = Math.max(0, (secondsLeft / totalSeconds) * 100);

  return (
    <div className="om-overlay">
      <div className="om-card">

        {/* ── Gradient header ── */}
        <div className="om-header">
          <div className="om-icon-ring">
            <i className="bi bi-shield-lock" />
          </div>
          <h3 className="om-heading">{heading}</h3>
          {/* <p className="om-subheading">
            OTP expires in <span>{fmt(secondsLeft).replace(" left", "")}</span>
          </p> */}
          <div className="om-timer-bar-wrap">
            <div
              className={`om-timer-bar${isExpired ? " expired" : isWarning ? " warning" : ""}`}
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>

        {/* ── Body ── */}
        <div className="om-body">

          {/* Timer pill */}
          <div className={`om-timer-pill${isExpired ? " expired" : isWarning ? " warning" : ""}`}>
            <i className="bi bi-clock" />
            <span>{fmt(secondsLeft)}</span>
          </div>

          {/* Target */}
          <div className="om-target-row">
            <p className="om-desc">{description}</p>
            {target && <span className="om-target">{target}</span>}
          </div>

          {/* OTP boxes */}
          <div className={`om-boxes${shaking ? " shake" : ""}`}>
            {otp.map((digit, index) => (
              <input
                key={index}
                id={`otp-${index}`}
                type="text"
                inputMode="numeric"
                maxLength="1"
                value={digit}
                onChange={(e) => onOtpChange(e.target.value, index)}
                onKeyDown={(e) => onOtpKeyDown(e, index)}
                className={`om-box${digit ? " om-box--filled" : ""}`}
                disabled={isExpired || outOfAttempts}
                autoFocus={index === 0}
              />
            ))}
          </div>

          {/* Error status */}
          {verifyError && !isExpired && (
            <div className="om-status om-status--error">
              <i className="bi bi-x-circle" /> {verifyError}
            </div>
          )}
          {isExpired && (
            <div className="om-status om-status--error">
              <i className="bi bi-clock" /> OTP expired — please request a new one
            </div>
          )}

          {/* Resend */}
          <div className="om-resend-row">
            <span>Didn't receive it?</span>
            <button
              className="om-resend-btn"
              disabled={(!canResend && !isExpired) || otpLoading}
              onClick={handleResend}
            >
              <i className="bi bi-arrow-counterclockwise" />
              {canResend || isExpired
                ? "Resend OTP"
                : `Resend OTP (${secondsLeft}s)`}
            </button>
          </div>

          {/* Actions */}
          <div className="om-actions">
            <button className="om-cancel-btn" onClick={onCancel}>
              <i className="bi bi-x" /> Cancel
            </button>
            <button
              className={`om-submit-btn${submitState === "success" ? " om-submit-btn--success" : ""}`}
              onClick={handleVerify}
              disabled={!allFilled || isExpired || outOfAttempts || submitState === "loading" || submitState === "success"}
            >
              {submitState === "loading" && <><span className="om-spinner" /> Verifying…</>}
              {submitState === "success" && <><i className="bi bi-check2" /> Verified!</>}
              {submitState === "idle"    && <><i className="bi bi-shield-check" /> Verify OTP</>}
            </button>
          </div>

        </div>
      </div>
    </div>
  );
};

export default OtpModal;