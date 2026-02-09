# Mobile App API Integration Guide

## Base URL
`https://<your-domain>/api/mobile`

## Authentication
Currently, all endpoints require a `tenantId` query parameter to identify the tenant.

---

## Endpoints

### 1. Get All Stock Groups
Fetches a list of all stock groups for a specific tenant.

- **URL**: `/groups`
- **Method**: `GET`
- **Query Parameters**:
  - `tenantId` (Long, Required): The ID of the tenant.

**Response Example**:
```json
[
  {
    "id": 1,
    "tenantId": 101,
    "companyId": "comp-001",
    "guid": "guid-group-1",
    "name": "Electronics",
    "parentGuid": "",
    "parentName": "",
    "isReserved": false
  },
  {
    "id": 2,
    "tenantId": 101,
    "companyId": "comp-001",
    "guid": "guid-group-2",
    "name": "Mobile Phones",
    "parentGuid": "guid-group-1",
    "parentName": "Electronics",
    "isReserved": false
  }
]
```

---

### 2. Get All Stock Categories
Fetches a list of all stock categories for a specific tenant.

- **URL**: `/categories`
- **Method**: `GET`
- **Query Parameters**:
  - `tenantId` (Long, Required): The ID of the tenant.

**Response Example**:
```json
[
  {
    "id": 1,
    "tenantId": 101,
    "companyId": "comp-001",
    "guid": "guid-cat-1",
    "name": "Premium",
    "parentGuid": "",
    "parentName": ""
  }
]
```

---

### 3. Get Stock Items
Fetches a paginated list of stock items. Supports filtering by Stock Group or Category.

- **URL**: `/items`
- **Method**: `GET`
- **Query Parameters**:
  - `tenantId` (Long, Required): The ID of the tenant.
  - `stockGroupGuid` (String, Optional): GUID of the stock group to filter by.
  - `categoryName` (String, Optional): Name of the category to filter by.
  - `page` (Integer, Optional): Page number (0-indexed, default 0).
  - `size` (Integer, Optional): Number of items per page (default 20).

**Response Example**:
```json
{
  "content": [
    {
      "id": 10,
      "tenantId": 101,
      "companyId": "comp-001",
      "guid": "guid-item-1",
      "name": "iPhone 15",
      "alias": "App-Mobile",
      "stockGroupGuid": "guid-group-2",
      "stockGroupName": "Mobile Phones",
      "categoryName": "Premium",
      "unitName": "Nos",
      "alternateUnit": null,
      "conversionFactor": null,
      "openingQuantity": 10.0,
      "openingRate": 70000.0,
      "openingValue": 700000.0,
      "gstHsnCode": "8517",
      "gstTaxability": "Taxable",
      "gstRate": 18.0,
      "isBatchwise": true,
      "isGodownTracking": true,
      "isReserved": false
    }
  ],
  "pageable": {
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "pageNumber": 0,
    "pageSize": 20,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalPages": 1,
  "totalElements": 1,
  "size": 20,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "first": true,
  "numberOfElements": 1,
  "empty": false
}
```
