import React, { useState, useMemo, useCallback, useRef, useEffect } from 'react';
import {
  useReactTable,
  getCoreRowModel,
  getSortedRowModel,
  getFilteredRowModel,
  flexRender,
} from '@tanstack/react-table';
import Pagination from './Pagination';
import TableActions from './TableActions';
import DateTimeFormatter from './DateTimeFormatter';
import { capitalizeEachWord } from '../../../hooks/useCommonFunctions';
import './DataTable.css';

const DataTable = ({
  data = { content: [], totalElements: 0, totalPages: 0, number: 0, size: 10 },
  columns = [],
  fetchData,
  loading = false,
  primaryKeys = ['id'],
  showActions = true,
  showEditButton = false,
  showViewButton = false,
  showApproveButton = false,
  showDeleteButton = false,
  showRejectButton = false,
  showIssueButton = false,
  editButtonDisabled = false,
  viewButtonDisabled = false,
  approveButtonDisabled = false,
  rejectButtonDisabled = false,
  deleteButtonDisabled = false,
  issueButtonDisabled = false,
  onEdit,
  onView,
  onDelete,
  onIssue,
  onApprove,
  onReject,
  basePath = '',
  className = '',
  selectableRows = false,
  customActions = [],
  onSelectionChange,
  // Row expansion props
  expandableRows = false,
  renderExpandedContent,
}) => {
  // Pagination state - sync with backend response
  const [pagination, setPagination] = useState({
    pageIndex: data.number || 0,
    pageSize: data.size || 10,
  });
  const [sorting, setSorting] = useState([]);
  const [globalFilter, setGlobalFilter] = useState('');
  // State for search input and trigger
  const [searchInput, setSearchInput] = useState('');
  const [searchTrigger, setSearchTrigger] = useState(0);

  // State for expanded rows (internal only)
  const [internalExpandedRows, setInternalExpandedRows] = useState([]);

  // State for row selection
  const [rowSelection, setRowSelection] = useState({});

  // Use ref to prevent unnecessary re-renders and stale references
  const fetchDataRef = useRef(fetchData);
  fetchDataRef.current = fetchData;

  // Enhanced columns with formatting logic, actions, and selection
  const enhancedColumns = useMemo(() => {
    const baseColumns = [];

    // Add selection column if enabled
    if (selectableRows) {
      baseColumns.push({
        id: 'selection',
        header: ({ table }) => (
          <input
            type="checkbox"
            checked={table.getIsAllRowsSelected()}
            onChange={table.getToggleAllRowsSelectedHandler()}
            aria-label="Select all rows"
          />
        ),
        cell: ({ row }) => (
          <input
            type="checkbox"
            checked={row.getIsSelected()}
            onChange={row.getToggleSelectedHandler()}
            aria-label={`Select row ${row.id}`}
          />
        ),
        enableSorting: false,
        enableColumnFilter: false,
      });
    }

    // Add expandable column if enabled
    if (expandableRows) {
      baseColumns.push({
        id: 'expander',
        header: '',
        cell: ({ row }) => (
          <button
            className="row-expander-button"
            onClick={() => handleRowExpand(row.id)}
            aria-label="Expand row"
          >
            <i
              className={`bi ${internalExpandedRows.includes(row.id) ? 'bi-chevron-up' : 'bi-chevron-down'}`}
            />
          </button>
        ),
        enableSorting: false,
        enableColumnFilter: false,
      });
    }

    // Add regular columns
    baseColumns.push(...columns.map(col => ({
      ...col,
      accessorKey: col.accessorKey || col.field,
      header: col.header,
      cell: col.Cell || (({ getValue, row }) => {
        const value = getValue();

        // Formatting logic based on column type
        if (col.accessorKey === 'isView') {
          // Handle isView status: 0 = Pending, 1 = Approved, 2 = Rejected
          const statusConfig = {
            0: { text: 'Pending', class: 'status-badge status-badge--pending' },
            1: { text: 'Approved', class: 'status-badge status-badge--approve' },
            2: { text: 'Rejected', class: 'status-badge status-badge--reject' }
          };
          const status = statusConfig[value] || { text: 'Unknown', class: 'status-badge' };
          return <span className={status.class}>{status.text}</span>;
        } else if (col.accessorKey === 'status' || col.type === 'status') {
          // Handle generic status field
          const statusValue = String(value).toLowerCase();
          let statusConfig = { text: value, class: 'status-badge' };

          if (statusValue === 'approved' || statusValue === 'approve') {
            statusConfig = { text: 'Approved', class: 'status-badge status-badge--approve' };
          } else if (statusValue === 'rejected' || statusValue === 'reject') {
            statusConfig = { text: 'Rejected', class: 'status-badge status-badge--reject' };
          } else if (statusValue === 'pending') {
            statusConfig = { text: 'Pending', class: 'status-badge status-badge--pending' };
          }
          else if (statusValue === 'active') {
            statusConfig = { text: 'Active', class: 'status-badge status-badge--active' };
          }
          else if (statusValue === 'inactive') {
            statusConfig = { text: 'Inactive', class: 'status-badge status-badge--inactive' };
          }

          return <span className={statusConfig.class}>{statusConfig.text}</span>;
        } else if (['date', 'datetime'].includes(col.type)) {
          return <DateTimeFormatter value={value} showTime={col.type === 'datetime'} />;
        } else if (['integer', 'float', 'double'].includes(col.type)) {
          return <div style={{ textAlign: 'left' }}>{formatNumber(value, col.type)}</div>;
        } else if (col.type === 'amount') {
          return (
            <div style={{ display: 'flex', justifyContent: 'flex-end', fontWeight: 'bold' }}>
              <span>₹{formatAmount(value)}</span>
            </div>
          );
        } else if (col.type === 'boolean') {
          // Render booleans/status as visible text so true/false booleans will render
          return <div style={{ textAlign: 'left' }}>{value === true ? 'Yes' : value === false ? 'No' : ''}</div>;
        } else if (col.type === 'serial') {
          return <div style={{ textAlign: 'left' }}>{(pagination.pageIndex * pagination.pageSize) + (row.index + 1)}</div>;
        } else if (col.type === 'text' || col.type === 'status') {
          return <div style={{ textAlign: 'left' }}>{capitalizeEachWord(value)}</div>;
        }
        return <div style={{ textAlign: 'left' }}>{value}</div>;
      }),
      // Use column definition flags for sorting and filtering
      enableSorting: col.sortable !== false,
      enableColumnFilter: col.filterable !== false,
    })));

    // Add actions column if enabled
    if (showActions) {
      baseColumns.push({
        id: 'actions',
        header: 'Actions',
        enableSorting: false,
        enableColumnFilter: false,
        cell: ({ row }) => {
          const rowData = row.original;
          const actionPath = primaryKeys.map(key => rowData[key]).join('/');

          return (
            <TableActions
              rowData={rowData}
              basePath={basePath}
              actionPath={actionPath}
              showEditButton={showEditButton}
              showViewButton={showViewButton}
              showDeleteButton={showDeleteButton}
              showIssueButton={showIssueButton}
              showApproveButton={showApproveButton}
              showRejectButton={showRejectButton}
              editButtonDisabled={editButtonDisabled}
              viewButtonDisabled={viewButtonDisabled}
              deleteButtonDisabled={deleteButtonDisabled}
              issueButtonDisabled={issueButtonDisabled}
              approveButtonDisabled={approveButtonDisabled}
              rejectButtonDisabled={rejectButtonDisabled}
              onEdit={onEdit}
              onView={onView}
              onDelete={onDelete}
              onIssue={onIssue}
              onApprove={onApprove}
              onReject={onReject}
            />
          );
        },
      });
    }

    return baseColumns;
  }, [columns, selectableRows, showActions, showEditButton, showViewButton, showApproveButton, showRejectButton, showIssueButton, showDeleteButton, editButtonDisabled, viewButtonDisabled, approveButtonDisabled, rejectButtonDisabled, issueButtonDisabled, deleteButtonDisabled, basePath, primaryKeys, onApprove, onReject, onEdit, onView, onDelete, onIssue, expandableRows, internalExpandedRows]);

  // TanStack Table configuration - Manual mode only
  const table = useReactTable({
    data: data.content || [],
    columns: enhancedColumns,
    manualPagination: true,
    manualSorting: true,
    manualFiltering: true,
    pageCount: data.totalPages,
    state: {
      pagination,
      sorting,
      globalFilter,
      rowSelection,
    },
    onPaginationChange: setPagination,
    onSortingChange: setSorting,
    onGlobalFilterChange: setGlobalFilter,
    onRowSelectionChange: setRowSelection,
    enableRowSelection: selectableRows,
    getRowId: (row) => primaryKeys.map(key => row[key]).join('_'), // Use primary keys for row IDs
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
  });

  // Notify parent of selection changes
  useEffect(() => {
    if (onSelectionChange && selectableRows) {
      const selectedRows = table.getSelectedRowModel().rows.map(row => row.original);
      onSelectionChange(selectedRows);
    }
  }, [rowSelection, onSelectionChange, selectableRows, table]);

  // Sync pagination state with backend response
  useEffect(() => {
    setPagination({
      pageIndex: data.number || 0,
      pageSize: data.size || 10,
    });
  }, [data.number, data.size]);

  // Fetch data when table state changes
  const fetchDataCallback = useCallback(() => {
    if (fetchDataRef.current) {
      const sortField = sorting[0]?.id || 'id';
      const sortOrder = 'desc';
      fetchDataRef.current({
        page: pagination.pageIndex,
        size: pagination.pageSize,
        sortField,
        sortOrder,
        filters: { search: globalFilter },
      });
    }
  }, [pagination, sorting, globalFilter]);

  // Handle search button click
  const handleSearchClick = useCallback(() => {
    setGlobalFilter(searchInput);
    setSearchTrigger(prev => prev + 1); // Trigger re-render
  }, [searchInput]);

  // Handle row expansion
  const handleRowExpand = useCallback((rowId) => {
    if (internalExpandedRows.includes(rowId)) {
      // Collapse row
      setInternalExpandedRows(prev => prev.filter(id => id !== rowId));
    } else {
      // Expand row
      setInternalExpandedRows(prev => [...prev, rowId]);
    }
  }, [internalExpandedRows]);

  // Handle search input change
  const handleSearchInputChange = useCallback((e) => {
    setSearchInput(e.target.value);
  }, []);

  // Handle search input key press
  const handleSearchKeyPress = useCallback((e) => {
    if (e.key === 'Enter') {
      handleSearchClick();
    }
  }, [handleSearchClick]);

  // Trigger fetch when pagination, sorting, or search changes
  useEffect(() => {
    fetchDataCallback();
  }, [fetchDataCallback, searchTrigger]);

  // Formatting functions
  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };

  const formatNumber = (value, type) => {
    if (value === null || value === undefined) return '';
    const num = Number(value);
    if (type === 'float' || type === 'double') {
      return num.toFixed(2);
    }
    return num.toLocaleString('en-IN');
  };

  const formatAmount = (value) => {
    if (value === null || value === undefined) return '₹0.00';
    const num = parseFloat(value);
    return num.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  };

  return (
    <div className={`data-table-container ${className}`}>

      {/* Table */}
      <div className="table-wrapper">
        <table className="data-table">
          <thead>
            {table.getHeaderGroups().map(headerGroup => (
              <tr key={headerGroup.id}>
                {headerGroup.headers.map(header => (
                  <th
                    key={header.id}
                    onClick={header.column.getCanSort() ? header.column.getToggleSortingHandler() : undefined}
                    className={header.column.getCanSort() ? 'sortable' : ''}
                  >
                    {header.isPlaceholder
                      ? null
                      // render header content
                      : flexRender(
                        header.column.columnDef.header,
                        header.getContext()
                      )}
                  </th>
                ))}
              </tr>
            ))}
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={enhancedColumns.length} className="loading-row">
                  <div className="loading-spinner">Loading...</div>
                </td>
              </tr>
            ) : table.getRowModel().rows.length === 0 ? (
              <tr>
                <td colSpan={enhancedColumns.length} className="no-data-row">
                  No data available
                </td>
              </tr>
            ) : (
              table.getRowModel().rows.map(row => (
                <React.Fragment key={row.id}>
                  <tr key={row.id} className="table-row">
                    {row.getVisibleCells().map(cell => (
                      <td key={cell.id}>
                        {flexRender(cell.column.columnDef.cell, cell.getContext())}
                      </td>
                    ))}
                  </tr>
                  {/* Expanded content row */}
                  {expandableRows && internalExpandedRows.includes(row.id) && (
                    <tr key={`expanded-${row.id}`} className="expanded-row">
                      <td colSpan={enhancedColumns.length} className="expanded-content">
                        {(() => {
                          try {
                            // Handle both string and array nestedData
                            let nestedData = row.original.nestedData || '[]';

                            // If nestedData is already an array, use it directly
                            if (Array.isArray(nestedData)) {
                              // Already parsed array
                            } else if (typeof nestedData === 'string') {
                              // Parse string to array
                              nestedData = JSON.parse(nestedData);
                            } else {
                              // Invalid format, set to empty array
                              nestedData = [];
                            }

                            if (!nestedData || nestedData.length === 0) {
                              return (
                                <div className="expanded-content-default">
                                  <strong>No nested data available</strong>
                                </div>
                              );
                            }

                            return (
                              <div className="nested-table-container">
                                <div className="nested-table-header">
                                  <span className="nested-count">{nestedData.length} records</span>
                                </div>

                                <table className="data-table">
                                  <tbody>
                                    {nestedData.map((item, rowIndex) => (
                                      <tr key={item.id || rowIndex} className="table-row">
                                        {columns.map((column, colIndex) => (
                                          <td key={colIndex}>
                                            {(() => {
                                              const value = item[column.accessorKey];
                                              const col = column;

                                              // Use the exact same cell rendering logic as main table
                                              if (col.accessorKey === 'isView') {
                                                // Handle isView status: 0 = Pending, 1 = Approved, 2 = Rejected
                                                const statusConfig = {
                                                  0: { text: 'Pending', class: 'status-badge status-badge--pending' },
                                                  1: { text: 'Approved', class: 'status-badge status-badge--approve' },
                                                  2: { text: 'Rejected', class: 'status-badge status-badge--reject' }
                                                };
                                                const status = statusConfig[value] || { text: 'Unknown', class: 'status-badge' };
                                                return <span className={status.class}>{status.text}</span>;
                                              } else if (col.accessorKey === 'status' || col.type === 'status') {
                                                // Handle generic status field
                                                const statusValue = String(value).toLowerCase();
                                                let statusConfig = { text: value, class: 'status-badge' };

                                                if (statusValue === 'approved' || statusValue === 'approve') {
                                                  statusConfig = { text: 'Approved', class: 'status-badge status-badge--approve' };
                                                } else if (statusValue === 'rejected' || statusValue === 'reject') {
                                                  statusConfig = { text: 'Rejected', class: 'status-badge status-badge--reject' };
                                                } else if (statusValue === 'pending') {
                                                  statusConfig = { text: 'Pending', class: 'status-badge status-badge--pending' };
                                                }
                                                else if (statusValue === 'active') {
                                                  statusConfig = { text: 'Active', class: 'status-badge status-badge--active' };
                                                }
                                                else if (statusValue === 'inactive') {
                                                  statusConfig = { text: 'Inactive', class: 'status-badge status-badge--inactive' };
                                                }
                                                return <span className={statusConfig.class}>{statusConfig.text}</span>;
                                              } else if (['date', 'datetime'].includes(col.type)) {
                                                return <DateTimeFormatter value={value} showTime={col.type === 'datetime'} />;
                                              } else if (['integer', 'float', 'double'].includes(col.type)) {
                                                return <div style={{ textAlign: 'left' }}>{formatNumber(value, col.type)}</div>;
                                              } else if (col.type === 'amount') {
                                                return (
                                                  <div style={{ display: 'flex', justifyContent: 'flex-end', fontWeight: 'bold' }}>
                                                    <span>₹{formatAmount(value)}</span>
                                                  </div>
                                                );
                                              } else if (col.type === 'boolean') {
                                                // Render booleans/status as visible text so true/false booleans will render
                                                return <div style={{ textAlign: 'left' }}>{value === true ? 'Yes' : value === false ? 'No' : ''}</div>;
                                              } else if (col.type === 'text' || col.type === 'status') {
                                                return <div style={{ textAlign: 'left' }}>{capitalizeEachWord(value)}</div>;
                                              }
                                              return <div style={{ textAlign: 'left' }}>{value}</div>;
                                            })()}
                                          </td>
                                        ))}

                                        {/* Add Actions column for nested table */}
                                        {showActions && (
                                          <td key="actions" className="actions-cell">
                                            <TableActions
                                              rowData={item}
                                              basePath={basePath}
                                              actionPath={primaryKeys.map(key => item[key]).join('/')}
                                              showEditButton={showEditButton}
                                              showViewButton={showViewButton}
                                              showApproveButton={showApproveButton}
                                              showRejectButton={showRejectButton}
                                              showDeleteButton={showDeleteButton}
                                              showIssueButton={showIssueButton}
                                              editButtonDisabled={editButtonDisabled}
                                              viewButtonDisabled={viewButtonDisabled}
                                              approveButtonDisabled={approveButtonDisabled}
                                              rejectButtonDisabled={rejectButtonDisabled}
                                              deleteButtonDisabled={deleteButtonDisabled}
                                              issueButtonDisabled={issueButtonDisabled}
                                              onEdit={onEdit}
                                              onView={onView}
                                              onDelete={onDelete}
                                              onApprove={onApprove}
                                              onReject={onReject}
                                              onIssue={onIssue}
                                              customActions={customActions}
                                            />
                                          </td>
                                        )}
                                      </tr>
                                    ))}
                                  </tbody>
                                </table>
                              </div>
                            );
                          } catch (error) {
                            return (
                              <div className="expanded-content-default">
                                <strong>Error parsing nested data:</strong>
                                <pre>{error.message}</pre>
                              </div>
                            );
                          }
                        })()}
                      </td>
                    </tr>
                  )}
                </React.Fragment>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <Pagination
        table={table}
        totalElements={data.totalElements}
        pageIndex={pagination.pageIndex}
        pageSize={pagination.pageSize}
        onPageChange={(newPageIndex) => {
          setPagination(prev => ({ ...prev, pageIndex: newPageIndex }));
        }}
        onPageSizeChange={(newPageSize) => {
          setPagination({ pageIndex: 0, pageSize: newPageSize });
        }}
      />
    </div>
  );
};

export default DataTable;
