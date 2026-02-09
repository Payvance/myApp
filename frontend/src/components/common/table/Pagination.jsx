import React from 'react';
import './DataTable.css';

const Pagination = ({
  table,
  totalElements = 0,
  pageIndex = 0,
  pageSize = 10,
  onPageChange,
  onPageSizeChange,
  pageSizeOptions = [5, 10, 20, 50],
}) => {
  const currentPage = pageIndex + 1;
  const totalPages = Math.ceil(totalElements / pageSize);
  const startRecord = pageIndex * pageSize + 1;
  const endRecord = Math.min((pageIndex + 1) * pageSize, totalElements);

  const handleFirstPage = () => {
    onPageChange(0);
  };

  const handlePreviousPage = () => {
    onPageChange(pageIndex - 1);
  };

  const handleNextPage = () => {
    onPageChange(pageIndex + 1);
  };

  const handleLastPage = () => {
    onPageChange(totalPages - 1);
  };

  const handlePageSizeChange = (event) => {
    const newPageSize = parseInt(event.target.value, 10);
    onPageSizeChange(newPageSize);
  };

  const handlePageJump = (event) => {
    const page = parseInt(event.target.value, 10);
    if (page >= 1 && page <= totalPages) {
      onPageChange(page - 1);
    }
  };

  return (
    <div className="pagination-container">
      {/* Records info */}
      <span className="pagination-info">
        Showing {startRecord}-{endRecord} of {totalElements} records
      </span>

      {/* Page size selector */}
      <select
        value={pageSize}
        onChange={handlePageSizeChange}
        className="pagination-page-size"
      >
        {pageSizeOptions.map((size) => (
          <option key={size} value={size}>
            {size} / page
          </option>
        ))}
      </select>

      {/* Page navigation */}
      <div className="pagination-nav">
        <button
          onClick={handleFirstPage}
          disabled={pageIndex === 0}
          className="pagination-button"
          title="First Page"
        >
          ««
        </button>

        <button
          onClick={handlePreviousPage}
          disabled={pageIndex === 0}
          className="pagination-button"
          title="Previous Page"
        >
          ‹
        </button>

        {/* Current page indicator */}
        <span className="pagination-current">
          {currentPage} / {totalPages || 1}
        </span>

        <button
          onClick={handleNextPage}
          disabled={pageIndex >= totalPages - 1}
          className="pagination-button"
          title="Next Page"
        >
          ›
        </button>

        <button
          onClick={handleLastPage}
          disabled={pageIndex >= totalPages - 1}
          className="pagination-button"
          title="Last Page"
        >
          »»
        </button>
      </div>

      {/* Jump to page input */}
      <div className="pagination-jump">
        <span>Page:</span>
        <input
          type="number"
          min={1}
          max={totalPages}
          value={currentPage}
          onChange={handlePageJump}
          className="pagination-input"
        />
      </div>
    </div>
  );
};

export default Pagination;
