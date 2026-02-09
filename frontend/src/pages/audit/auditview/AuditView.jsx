import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import SuperAdminLayout from '../../../layouts/SuperAdminLayout';
import PageHeader from '../../../components/common/pageheader/PageHeader';
import { auditServices } from '../../../services/apiService';
// imported form config
import formConfig from '../../../config/formConfig';
import './AuditView.css';

const AuditView = () => {
    const { id } = useParams();
    const [log, setLog] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    // imported form config
    const { auditview } = formConfig;

    useEffect(() => {
        const fetchLog = async () => {
            try {
                setLoading(true);
                const response = await auditServices.getAuditLogById(id);
                setLog(response.data);
                setError(null);
            } catch (err) {
             
                setError("Failed to load audit log details. Please try again later.");
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchLog();
        }
    }, [id]);

    return (
        <SuperAdminLayout>
            <div className="audit-view-container">
                <PageHeader
                    title="Audit Log Details"
                    subtitle="View detailed information about this activity"
                    showBackButton={true}
                />

                <div className="audit-view-content">
                    {loading ? (
                        <div className="audit-loading">
                            <div className="spinner"></div>
                            <p>Loading audit details...</p>
                        </div>
                    ) : error ? (
                        <div className="audit-error">
                            <p>{error}</p>
                        </div>
                    ) : (
                        <div className="audit-detail-card">
                            <div className="detail-section">
                                <h3>{auditview.basicInformation.label}</h3>
                                <div className="detail-grid">
                                    <div className="detail-item">
                                        <label>{auditview.logId.label}</label>
                                        <span>{log.id}</span>
                                    </div>
                                    <div className="detail-item">
                                        <label>{auditview.timestamp.label}</label>
                                        <span>{new Date(log.createdAt).toLocaleString()}</span>
                                    </div>
                                    <div className="detail-item">
                                        <label>{auditview.action.label}</label>
                                        <span className={`action-badge ${log.action.toLowerCase()}`}>
                                            {log.action}
                                        </span>
                                    </div>
                                    <div className="detail-item">
                                        <label>{auditview.performedBy.label}</label>
                                        <span>{log.actorType || 'N/A'} {log.actorId}</span>
                                    </div>
                                    <div className="detail-item">
                                        <label>{auditview.module.label}</label>
                                        <span>{log.entityType || 'N/A'}</span>
                                    </div>
                                   
                                </div>
                            </div>

                            <div className="detail-section">
                                <h3>{auditview.metadata.label}</h3>
                                <div className="meta-container">
                                    {log.metaJson ? (
                                        <div className="meta-json-wrapper">
                                            <pre>{JSON.stringify(JSON.parse(log.metaJson), null, 2)}</pre>
                                        </div>
                                    ) : (
                                        <p className="no-meta">{auditview.noMetadata.label}</p>
                                    )}
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </SuperAdminLayout>
    );
};

export default AuditView;
