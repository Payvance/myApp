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
  expandableRows = false,
  renderExpandedContent,
}) => {
  const [pagination, setPagination] = useState({
    pageIndex: data.number || 0,
    pageSize: data.size || 10,
  });
  const [sorting, setSorting] = useState([]);
  const [globalFilter, setGlobalFilter] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [searchTrigger, setSearchTrigger] = useState(0);
  const [internalExpandedRows, setInternalExpandedRows] = useState([]);
  const [rowSelection, setRowSelection] = useState({});
  const fetchDataRef = useRef(fetchData);
  fetchDataRef.current = fetchData;

  const renderCellValue = (col, value, row, pagination) => {
    if (col.accessorKey === 'isView') {
      const statusConfig = {
        0: { text: 'Pending', class: 'status-badge status-badge--pending' },
        1: { text: 'Approved', class: 'status-badge status-badge--approve' },
        2: { text: 'Rejected', class: 'status-badge status-badge--reject' }
      };
      const status = statusConfig[value] || { text: 'Unknown', class: 'status-badge' };
      return <span className={status.class}>{status.text}</span>;
    } else if (col.accessorKey === 'status' || col.type === 'status') {
      const statusValue = String(value).toLowerCase();
      let statusConfig = { text: value, class: 'status-badge' };
      if (statusValue === 'approved' || statusValue === 'approve') statusConfig = { text: 'Approved', class: 'status-badge status-badge--approve' };
      else if (statusValue === 'rejected' || statusValue === 'reject') statusConfig = { text: 'Rejected', class: 'status-badge status-badge--reject' };
      else if (statusValue === 'pending') statusConfig = { text: 'Pending', class: 'status-badge status-badge--pending' };
      else if (statusValue === 'active') statusConfig = { text: 'Active', class: 'status-badge status-badge--active' };
      else if (statusValue === 'inactive') statusConfig = { text: 'Inactive', class: 'status-badge status-badge--inactive' };
      return <span className={statusConfig.class}>{statusConfig.text}</span>;
    } else if (['date', 'datetime'].includes(col.type)) {
      return <DateTimeFormatter value={value} showTime={col.type === 'datetime'} />;
    } else if (['integer', 'float', 'double'].includes(col.type)) {
      return <div style={{ textAlign: 'left' }}>{formatNumber(value, col.type)}</div>;
    } else if (col.type === 'amount') {
      return <div style={{ display: 'flex', justifyContent: 'flex-end', fontWeight: 'bold' }}><span>₹{formatAmount(value)}</span></div>;
    } else if (col.type === 'boolean') {
      return <div style={{ textAlign: 'left' }}>{value === true ? 'Yes' : value === false ? 'No' : ''}</div>;
    } else if (col.type === 'serial') {
      return <div style={{ textAlign: 'left' }}>{(pagination.pageIndex * pagination.pageSize) + (row.index + 1)}</div>;
    } else if (col.type === 'text') {
      return <div style={{ textAlign: 'left' }}>{capitalizeEachWord(value)}</div>;
    }
    return <div style={{ textAlign: 'left' }}>{value}</div>;
  };

  const enhancedColumns = useMemo(() => {
    const baseColumns = [];

    if (selectableRows) {
      baseColumns.push({
        id: 'selection',
        header: ({ table }) => (
          <input type="checkbox" checked={table.getIsAllRowsSelected()} onChange={table.getToggleAllRowsSelectedHandler()} aria-label="Select all rows" />
        ),
        cell: ({ row }) => (
          <input type="checkbox" checked={row.getIsSelected()} onChange={row.getToggleSelectedHandler()} aria-label={`Select row ${row.id}`} />
        ),
        enableSorting: false,
        enableColumnFilter: false,
      });
    }

    if (expandableRows) {
      baseColumns.push({
        id: 'expander',
        header: '',
        cell: ({ row }) => (
          <button className="row-expander-button" onClick={() => handleRowExpand(row.id)} aria-label="Expand row">
            <i className={`bi ${internalExpandedRows.includes(row.id) ? 'bi-chevron-up' : 'bi-chevron-down'}`} />
          </button>
        ),
        enableSorting: false,
        enableColumnFilter: false,
      });
    }

    baseColumns.push(...columns.map(col => ({
      ...col,
      accessorKey: col.accessorKey || col.field,
      header: col.header,
      cell: col.Cell || (({ getValue, row }) => renderCellValue(col, getValue(), row, pagination)),
      enableSorting: col.sortable !== false,
      enableColumnFilter: col.filterable !== false,
    })));

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
              rowData={rowData} basePath={basePath} actionPath={actionPath}
              showEditButton={showEditButton} showViewButton={showViewButton}
              showDeleteButton={showDeleteButton} showIssueButton={showIssueButton}
              showApproveButton={showApproveButton} showRejectButton={showRejectButton}
              editButtonDisabled={editButtonDisabled} viewButtonDisabled={viewButtonDisabled}
              deleteButtonDisabled={deleteButtonDisabled} issueButtonDisabled={issueButtonDisabled}
              approveButtonDisabled={approveButtonDisabled} rejectButtonDisabled={rejectButtonDisabled}
              onEdit={onEdit} onView={onView} onDelete={onDelete}
              onIssue={onIssue} onApprove={onApprove} onReject={onReject}
            />
          );
        },
      });
    }

    return baseColumns;
  }, [columns, selectableRows, showActions, showEditButton, showViewButton, showApproveButton, showRejectButton, showIssueButton, showDeleteButton, editButtonDisabled, viewButtonDisabled, approveButtonDisabled, rejectButtonDisabled, issueButtonDisabled, deleteButtonDisabled, basePath, primaryKeys, onApprove, onReject, onEdit, onView, onDelete, onIssue, expandableRows, internalExpandedRows]);

  const table = useReactTable({
    data: data.content || [],
    columns: enhancedColumns,
    manualPagination: true,
    manualSorting: true,
    manualFiltering: true,
    pageCount: data.totalPages,
    state: { pagination, sorting, globalFilter, rowSelection },
    onPaginationChange: setPagination,
    onSortingChange: setSorting,
    onGlobalFilterChange: setGlobalFilter,
    onRowSelectionChange: setRowSelection,
    enableRowSelection: selectableRows,
    getRowId: (row) => primaryKeys.map(key => row[key]).join('_'),
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
    getFilteredRowModel: getFilteredRowModel(),
  });

  useEffect(() => {
    if (onSelectionChange && selectableRows) {
      const selectedRows = table.getSelectedRowModel().rows.map(row => row.original);
      onSelectionChange(selectedRows);
    }
  }, [rowSelection, onSelectionChange, selectableRows, table]);

  useEffect(() => {
    setPagination({ pageIndex: data.number || 0, pageSize: data.size || 10 });
  }, [data.number, data.size]);

  const fetchDataCallback = useCallback(() => {
    if (fetchDataRef.current) {
      fetchDataRef.current({
        page: pagination.pageIndex,
        size: pagination.pageSize,
        sortField: sorting[0]?.id || 'id',
        sortOrder: 'desc',
        filters: { search: globalFilter },
      });
    }
  }, [pagination, sorting, globalFilter]);

  const handleSearchClick = useCallback(() => {
    setGlobalFilter(searchInput);
    setSearchTrigger(prev => prev + 1);
  }, [searchInput]);

  const handleRowExpand = useCallback((rowId) => {
    setInternalExpandedRows(prev =>
      prev.includes(rowId) ? prev.filter(id => id !== rowId) : [...prev, rowId]
    );
  }, []);

  const handleSearchInputChange = useCallback((e) => setSearchInput(e.target.value), []);

  const handleSearchKeyPress = useCallback((e) => {
    if (e.key === 'Enter') handleSearchClick();
  }, [handleSearchClick]);

  useEffect(() => { fetchDataCallback(); }, [fetchDataCallback, searchTrigger]);

  const formatNumber = (value, type) => {
    if (value === null || value === undefined) return '';
    const num = Number(value);
    return (type === 'float' || type === 'double') ? num.toFixed(2) : num.toLocaleString('en-IN');
  };

  const formatAmount = (value) => {
    if (value === null || value === undefined) return '0.00';
    return parseFloat(value).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  };

  // ── Helper: render a nested row using same column definitions ──
  const renderNestedRow = (item, rowIndex) => (
    <tr key={item.id || rowIndex} className="nested-data-row">
      {/* Spacer cell — matches expander column width, shows accent bar */}
      <td className="nested-spacer-cell" />

      {/* Data cells — one per column, matching parent columns exactly */}
      {columns.map((col, colIndex) => (
        <td key={colIndex} className="nested-data-cell">
          {renderCellValue(col, item[col.accessorKey || col.field], { index: rowIndex }, pagination)}
        </td>
      ))}

      {/* Actions cell if enabled */}
      {showActions && (
        <td className="nested-data-cell">
          <TableActions
            rowData={item}
            basePath={basePath}
            actionPath={primaryKeys.map(key => item[key]).join('/')}
            showEditButton={showEditButton} showViewButton={showViewButton}
            showApproveButton={showApproveButton} showRejectButton={showRejectButton}
            showDeleteButton={showDeleteButton} showIssueButton={showIssueButton}
            editButtonDisabled={editButtonDisabled} viewButtonDisabled={viewButtonDisabled}
            approveButtonDisabled={approveButtonDisabled} rejectButtonDisabled={rejectButtonDisabled}
            deleteButtonDisabled={deleteButtonDisabled} issueButtonDisabled={issueButtonDisabled}
            onEdit={onEdit} onView={onView} onDelete={onDelete}
            onApprove={onApprove} onReject={onReject} onIssue={onIssue}
            customActions={customActions}
          />
        </td>
      )}
    </tr>
  );

  return (
    <div className={`data-table-container ${className}`}>
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
                    {header.isPlaceholder ? null : flexRender(header.column.columnDef.header, header.getContext())}
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
                <td colSpan={enhancedColumns.length} className="no-data-row">No data available</td>
              </tr>
            ) : (
              table.getRowModel().rows.map(row => {
                const isExpanded = expandableRows && internalExpandedRows.includes(row.id);

                let nestedData = [];
                if (isExpanded) {
                  try {
                    const raw = row.original.nestedData || '[]';
                    nestedData = Array.isArray(raw) ? raw : JSON.parse(raw);
                  } catch { nestedData = []; }
                }

                return (
                  <React.Fragment key={row.id}>
                    {/* ── Parent row ── */}
                    <tr className="table-row">
                      {row.getVisibleCells().map(cell => (
                        <td key={cell.id}>
                          {flexRender(cell.column.columnDef.cell, cell.getContext())}
                        </td>
                      ))}
                    </tr>

                    {/* ── Nested rows — injected directly into same tbody ── */}
                    {isExpanded && nestedData.length === 0 && (
                      <tr className="nested-empty-row">
                        <td className="nested-spacer-cell" />
                        <td colSpan={enhancedColumns.length - 1} className="nested-data-cell" style={{ color: 'var(--text-tertiary)', fontStyle: 'italic' }}>
                          No nested records
                        </td>
                      </tr>
                    )}
                    {isExpanded && nestedData.map((item, idx) => renderNestedRow(item, idx))}

                    {/* ── Closing accent line after last nested row ── */}
                    {isExpanded && nestedData.length > 0 && (
                      <tr className="nested-cap-row">
                        <td colSpan={enhancedColumns.length} />
                      </tr>
                    )}
                  </React.Fragment>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      <Pagination
        table={table}
        totalElements={data.totalElements}
        pageIndex={pagination.pageIndex}
        pageSize={pagination.pageSize}
        onPageChange={(newPageIndex) => setPagination(prev => ({ ...prev, pageIndex: newPageIndex }))}
        onPageSizeChange={(newPageSize) => setPagination({ pageIndex: 0, pageSize: newPageSize })}
      />
    </div>
  );
};

export default DataTable;