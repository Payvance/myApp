import React, { useState } from "react";
import "./DocumentInputField.css";
import PopUp from "../popups/PopUp";
import Button from "../button/Button";

const DocumentInputField = ({
    label,
    name,
    onChange,
    value = null, // Pre-existing document (e.g. base64 or URL)
    accept = ".pdf,.jpg,.jpeg,.png",
    disabled = false,
    required = false,
    validationErrors = {},
}) => {
    const [selectedFile, setSelectedFile] = useState(null);
    const [isPreviewOpen, setIsPreviewOpen] = useState(false);
    const [previewContent, setPreviewContent] = useState(null);
    const [fileType, setFileType] = useState(null);
    const inputId = `doc-input-${name}`;

    const formatFileSize = (bytes) => {
        if (bytes === 0) return "0 Bytes";
        const k = 1024;
        const sizes = ["Bytes", "KB", "MB", "GB"];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
    };

    const handleInternalChange = (e) => {
        const file = e.target.files?.[0];
        if (file) {
            setSelectedFile(file);
        }
        if (onChange) {
            onChange(e);
        }
    };

    const handleClear = (e) => {
        e.preventDefault();
        setSelectedFile(null);
        // Reset the input value
        const input = document.getElementById(inputId);
        if (input) input.value = "";

        // Notify parent if needed
        if (onChange) {
            onChange({ target: { name, value: null, files: [] } });
        }
    };

    const handleViewExisting = () => {
        if (!value) return;

        let content = value;
        let type = "image";

        // If it's a data URL (base64)
        if (value.startsWith("data:") || value.length > 100) {
            if (value.startsWith("data:")) {
                type = value.includes("application/pdf") ? "pdf" : "image";
            } else {
                // Assume it's base64 but missing the prefix (common in this app)
                const isPdf = value.startsWith("JVBER");
                const mimeType = isPdf ? "application/pdf" : "image/png";
                content = `data:${mimeType};base64,${value}`;
                type = isPdf ? "pdf" : "image";
            }
        } else {
            // Assume it's a direct URL
            type = value.toLowerCase().endsWith(".pdf") ? "pdf" : "image";
        }

        setPreviewContent(content);
        setFileType(type);
        setIsPreviewOpen(true);
    };

    const handleDownload = () => {
        const link = document.createElement("a");
        link.href = previewContent;
        link.download = `document_${name}.${fileType === "pdf" ? "pdf" : "png"}`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    return (
        <div className="input-container document-input-container">
            <input
                type="file"
                name={name}
                id={inputId}
                accept={accept}
                onChange={handleInternalChange}
                disabled={disabled}
                placeholder=" "
                required={required}
            />
            <label htmlFor={inputId}>
                {label}
                {required && <span className="required">*</span>}
            </label>

            {(selectedFile || (value && !selectedFile)) && (
                <div className="file-info-badge">
                    <span className="file-name">
                        {selectedFile
                            ? (selectedFile.name.length > 25 ? selectedFile.name.substring(0, 22) + "..." : selectedFile.name)
                            : "Uploaded Document"}
                    </span>
                    {selectedFile && (
                        <span className="file-size">{formatFileSize(selectedFile.size)}</span>
                    )}

                    <div className="file-actions">
                        {!selectedFile && value && (
                            <button
                                type="button"
                                className="view-file-btn"
                                onClick={handleViewExisting}
                                title="View document"
                            >
                                <i className="bi bi-eye"></i>
                            </button>
                        )}
                        {!disabled && (
                            <button
                                type="button"
                                className="clear-file-btn"
                                onClick={handleClear}
                                title="Clear file"
                            >
                                &times;
                            </button>
                        )}
                    </div>
                </div>
            )}

            {validationErrors?.[name] && (
                <span className="error-message">{validationErrors[name]}</span>
            )}

            <PopUp
                isOpen={isPreviewOpen}
                onClose={() => setIsPreviewOpen(false)}
                title={`Preview: ${label}`}
                size="large"
            >
                <div className="document-preview-wrapper" style={{ display: 'flex', flexDirection: 'column', gap: '20px', height: '100%' }}>
                    <div className="preview-container" style={{ flex: 1, minHeight: '400px', border: '1px solid #ddd', borderRadius: '8px', overflow: 'hidden', backgroundColor: '#f9f9f9' }}>
                        {fileType === "pdf" ? (
                            <iframe
                                src={previewContent}
                                title="PDF Preview"
                                width="100%"
                                height="600px"
                                style={{ border: 'none' }}
                            />
                        ) : (
                            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', padding: '20px' }}>
                                <img
                                    src={previewContent}
                                    alt="Document Preview"
                                    style={{ maxWidth: '100%', maxHeight: '600px', objectFit: 'contain', boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}
                                />
                            </div>
                        )}
                    </div>
                    <div className="preview-actions" style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
                        <Button
                            text="Download"
                            onClick={handleDownload}
                            variant="secondary"
                        />
                        <Button
                            text="Close"
                            onClick={() => setIsPreviewOpen(false)}
                            variant="primary"
                        />
                    </div>
                </div>
            </PopUp>
        </div>
    );
};

export default DocumentInputField;
