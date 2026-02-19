import { useState, useRef, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import Button from "../../button/Button";
import PopUp from "../../popups/PopUp";
import { authServices, resetPasswordServices, userServices } from "../../../../services/apiService";
import "./SidebarProfile.css";
import { SUPPORT_INFO } from "../../../../config/Config";
import InputField from "../../inputfield/InputField";
import { toast } from "react-toastify";
import formConfig from "../../../../config/formConfig";
import PaswordInputBox from "../../inputfield/PaswordInputBox";

/* Utils */
const getInitials = (name = "") => {
  if (!name) return "?";
  const parts = name.trim().split(" ");
  return parts.length > 1
    ? (parts[0][0] + parts[1][0]).toUpperCase()
    : name.substring(0, 2).toUpperCase();
};

const UserProfileMenu = ({
  upgradeRoute = "/upgrade-plan",
  showUpgrade = false,
  showLicense = false,
  showPersonalization = true,
  showForgotPassword = true,
}) => {
   /* -------------------- STATE -------------------- */
  const [openMenu, setOpenMenu] = useState(false);
  const [openSupport, setOpenSupport] = useState(false);
  const [showLogoutPopup, setShowLogoutPopup] = useState(false);
  const [supportPosition, setSupportPosition] = useState("right");
  const [user, setUser] = useState({ name: "", email: "" });
  const [forgotStep, setForgotStep] = useState(0); // 0 = closed, 1 = old password, 2 = new password
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showResetPopup, setShowResetPopup] = useState(false);
  const [showOldPass, setShowOldPass] = useState(false);
  const [showNewPass, setShowNewPass] = useState(false);
  const [showConfirmPass, setShowConfirmPass] = useState(false);
  const [isPasswordValid, setIsPasswordValid] = useState(false); 

  const menuRef = useRef(null);
  const supportRef = useRef(null);
  const navigate = useNavigate();

  /* -------------------- EFFECTS -------------------- */

  /* Close on outside click */
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setOpenMenu(false);
        setOpenSupport(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  /* Fetch user data */
  useEffect(() => {
    const getUserData = async () => {
      try {
        const userId = localStorage.getItem("user_id");
        const response = await userServices.getUserData(userId);
        if (response?.data) {
          setUser({
            name: response.data.name || "",
            email: response.data.email || "",
          });
        }
      } catch (error) {
        console.error("Failed to fetch user data:", error);
      }
    };

    getUserData();
  }, []);


  /* -------------------- HANDLERS -------------------- */
  // Logout
  const handleLogout = async () => {
    setShowLogoutPopup(false);
    setOpenMenu(false);

    try {
      await authServices.logout();
      localStorage.removeItem("token");
      sessionStorage.removeItem("token");
      navigate("/signin");
    } catch (error) {
      console.error("Logout failed:", error);
    }
  };

  /* Support dropdown positioning */
  const toggleSupport = () => {
    setOpenSupport((prev) => !prev);

    if (!openSupport && supportRef.current) {
      const rect = supportRef.current.getBoundingClientRect();
      const viewportWidth = window.innerWidth;
      const supportWidth = 220;

      if (rect.right + supportWidth > viewportWidth) {
        setSupportPosition("left");
      } else {
        setSupportPosition("right");
      }
    }
  };

 /* Copy to clipboard */
  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text);
  };

 const handleUnifiedReset = async (e) => {
  e?.preventDefault();
  if (!oldPassword || !newPassword || !confirmPassword) {
    toast.error("All fields are required");
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
  if (oldPassword === newPassword) {
    toast.error("New password must be different from current password");
    return;
  }

  setLoading(true);
  try {
    const verifyRes = await resetPasswordServices.verifyAndResetPassword({
      currentPassword: oldPassword,
    });

    if (verifyRes.data === 1) {
      const resetRes = await resetPasswordServices.setNewPassword({
        newPassword: newPassword,
      });

      if (resetRes.data.message === "Password changed successfully") {
        toast.success("Password changed successfully");
        setShowResetPopup(false); // CLOSE POPUP
        handleLogout();
      } else {
        toast.error("Failed to update password");
      }
    } else {
      toast.error("Incorrect current password. Try again.");
    }
  } catch (err) {
    toast.error("An error occurred during password reset");
  } finally {
    setLoading(false);
  }
};

  /* -------------------- JSX -------------------- */

  return (
    <>
      {/* ================= Sidebar Footer START ================= */}
      <div className="sidebar-footer" ref={menuRef}>
        {/* -------- USER PROFILE START -------- */}
        <div
          className="user-profile"
          onClick={() => {
            setOpenMenu((prev) => !prev);
            setOpenSupport(false);
          }}
        >
          <span className="avatar">{getInitials(user.name)}</span>

          <div className="user-info">
            <span className="username" title={user.name}>
              {user.name}
            </span>
            <span className="role" title={user.email}>
              @{user.email}
            </span>
          </div>
        </div>
        {/* -------- USER PROFILE END -------- */}

        
        {/* -------- DROPDOWN START -------- */}
        {openMenu && (
          <div className="profile-dropdown">
            {showUpgrade && (
              <button
                className="dropdown-item upgrade-glow"
                 style={{ width: "100%" }}
                onClick={() => {
                  navigate(upgradeRoute);
                  setOpenMenu(false);
                }}
              >
                <i className="bi bi-star-fill" />
                <span>Upgrade plan</span>
              </button>
            )}

            {showLicense && (
              <Link
                to="/license"
                className="dropdown-item"
                onClick={() => setOpenMenu(false)}
              >
                <i className="bi bi-patch-check" />
                <span>License Information</span>
              </Link>
            )}

            {showPersonalization && (
              <Link
                to="/personalization"
                className="dropdown-item"
                onClick={() => setOpenMenu(false)}
              >
                <i className="bi bi-palette-fill" />
                <span>Personalization</span>
              </Link>
            )}

            <div className="dropdown-divider" />

            {/* -------- HELP SECTION START -------- */}
            <div className="help-wrapper" ref={supportRef}>
              <div className="dropdown-item" onClick={toggleSupport}>
                <i className="bi bi-life-preserver" />
                <span>Help</span>
                <i
                  className={`bi bi-chevron-${openSupport ? "left" : "right"
                    } ms-auto`}
                />
              </div>

              {openSupport && (
                <div
                  className={`support-box slide-fade support-${supportPosition}`}
                >
                  <h6 className="support-title">Support</h6>

                  <div className="support-item">
                    <i className="bi bi-envelope-fill" />
                    <span>{SUPPORT_INFO.email}</span>
                    <i
                      className="bi bi-clipboard copy-icon"
                      onClick={() => copyToClipboard(SUPPORT_INFO.email)}
                    />
                  </div>

                  <div className="support-item">
                    <i className="bi bi-telephone-fill" />
                    <span>{SUPPORT_INFO.phone}</span>
                    <i
                      className="bi bi-clipboard copy-icon"
                      onClick={() => copyToClipboard(SUPPORT_INFO.phone)}
                    />
                  </div>
                </div>
              )}
            </div>
              {/* -------- HELP SECTION END -------- */}

              {/* Reset Password START */}
            {showForgotPassword && (
              <Link

                className="dropdown-item"
                onClick={() => {
                  setShowResetPopup(true);
                  setOpenMenu(false);
                  setError("");
                  setOldPassword("");
                  setNewPassword("");
                  setConfirmPassword("");
                }}
              >
                <i className="bi bi-key-fill" />
                <span>Reset password</span>
              </Link>
            )}

            <button
              className="dropdown-item logout"
              onClick={() => setShowLogoutPopup(true)}
            >
              <i className="bi bi-box-arrow-right" />
              <span>Log out</span>
            </button>
            {/* Logout END */}
          </div>
        )}
      </div>
       {/* ================= Sidebar Footer END ================= */}

      {/* ================= UNIFIED RESET PASSWORD POPUP ================= */}
<PopUp
  isOpen={showResetPopup}
  onClose={() => setShowResetPopup(false)}
  title="Change Your Password"
  subtitle="Enter your current password and choose a new one."
  size="large"
>
  <div className="reset-password-form-container" style={{marginTop : "10px"}}>
    {/* CURRENT PASSWORD */}
    <div className="position-relative signup-pass-container" style={{width: "49%"}}>
      <InputField
        label={formConfig.signin.currentPassword.label}
        value={oldPassword}
        onChange={(e) => setOldPassword(e.target.value)}
        type={showOldPass ? "text" : "password"}
        classN="large"
        max={16}
      />
      <i className={`bi ${showOldPass ? "bi-eye" : "bi-eye-slash"} eye-icon-signup`} 
         onClick={() => setShowOldPass(!showOldPass)}></i>
    </div>

    {/* NEW PASSWORD (Validation box will now float to the right) */}
    <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
    <div className="position-relative signup-pass-container">
      <PaswordInputBox
        label="New Password"
        value={newPassword}
        onChange={setNewPassword} 
        type={showNewPass ? "text" : "password"}
        onValidationChange={setIsPasswordValid}
        validationType="PASSWORD"
        classN="large"
        showCaseInfo={true}
      />
      <i className={`bi ${showNewPass ? "bi-eye" : "bi-eye-slash"} eye-icon-signup`} 
         onClick={() => setShowNewPass(!showNewPass)}></i>    
    </div>

    {/* CONFIRM NEW PASSWORD */}
    <div className="position-relative signup-pass-container">
      <InputField
        label="Confirm New Password"
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.target.value)}
        type={showConfirmPass ? "text" : "password"}
        classN="large"
        max={16}
        onPaste={(e) => e.preventDefault()} // Disable paste for confirm password
      />
      <i className={`bi ${showConfirmPass ? "bi-eye" : "bi-eye-slash"} eye-icon-signup`} 
         onClick={() => setShowConfirmPass(!showConfirmPass)}></i>
    </div>
    </div>

    <div className="password-note-section">
      <strong>Note:</strong>
      <ul style={{ marginTop: "5px", paddingLeft: "18px" }}>
        <li>To keep your account safe, change your password every 180 days.</li>
        <li>Once your password is changed, you will be automatically logged out.</li>
      </ul>
    </div>

    <div className="popup-buttons">
      <Button
        text={loading ? "Processing..." : "Update Password"}
        onClick={handleUnifiedReset}
        disabled={loading || !oldPassword || !newPassword || !confirmPassword || !isPasswordValid }
      />
    </div>
  </div>
</PopUp>

      {/* ================= LOGOUT POPUP START ================= */}
      <PopUp
        isOpen={showLogoutPopup}
        onClose={() => setShowLogoutPopup(false)}
        title="Log Out"
        subtitle="Are you sure you want to log out of your account?"
        size="small"
      >
        <div className="popup-buttons" style={{marginTop : "10px"}}>
          <Button
            text="Cancel"
            variant="green-line"
            onClick={() => setShowLogoutPopup(false)}
          />
          <Button text="Logout" variant="red-line" onClick={handleLogout} />
        </div>
      </PopUp>
      {/* ================= LOGOUT POPUP END ================= */}
    </>
  );
};

export default UserProfileMenu;
