import React, { useState } from 'react';
import './DataTable.css';

const SearchFilter = ({
  searchFields = [],
  selectedField = '',
  onFieldChange,
  searchValue = '',
  onSearchChange,
  onSearch,
  onClear,
  placeholder = 'Search...',
  loading = false,
  className = '',
}) => {
  const [localSearchValue, setLocalSearchValue] = useState(searchValue);

  const handleInputChange = (event) => {
    const value = event.target.value;
    setLocalSearchValue(value);
    onSearchChange?.(value);
  };

  const handleKeyPress = (event) => {
    if (event.key === 'Enter') {
      onSearch?.();
    }
  };

  const handleClear = () => {
    setLocalSearchValue('');
    onSearchChange?.('');
    onClear?.();
  };

  const selectedFieldConfig = searchFields.find(field => field.name === selectedField);
  const inputType = selectedFieldConfig?.type || 'text';

  return (
    <div className={`search-filter-container ${className}`}>
      {/* Field Selector */}
      {searchFields.length > 1 && (
        <select
          value={selectedField}
          onChange={(e) => onFieldChange?.(e.target.value)}
          className="search-field-select"
        >
          {searchFields.map((field) => (
            <option key={field.name} value={field.name}>
              {field.label}
            </option>
          ))}
        </select>
      )}

      {/* Search Input */}
      <div className="search-input-wrapper">
        <input
          type={inputType === 'date' ? 'date' : 'text'}
          value={localSearchValue}
          onChange={handleInputChange}
          onKeyPress={handleKeyPress}
          placeholder={placeholder}
          className="search-input"
        />
        {localSearchValue && (
          <button
            onClick={handleClear}
            className="search-clear-button"
            title="Clear"
          >
            ✕
          </button>
        )}
      </div>

      {/* Search Button */}
      <button
        onClick={onSearch}
        disabled={loading || !localSearchValue.trim()}
        className="search-button"
      >
        🔍 Search
      </button>
    </div>
  );
};

export default SearchFilter;
