# Dashboard Component System

A comprehensive, role-based dashboard system built with React and Chart.js.

## 🚀 Features

- **Role-Based Data**: Different dashboard content for SuperAdmin, Vendor, Tenant, and CA roles
- **Responsive Design**: Works seamlessly on desktop, tablet, and mobile devices
- **Modular Architecture**: Reusable components for cards, charts, and data views
- **Loading States**: Skeleton loaders for better UX
- **Error Handling**: Graceful error display with recovery options
- **Modern UI**: Clean, professional design with smooth animations

## 📁 Structure

```
src/components/dashboard/
├── CommonDashboard.jsx      # Main dashboard component
├── DashboardLayout.jsx      # Layout wrapper
├── DashboardCard.jsx         # Reusable card component
├── PieChartComponent.jsx    # Pie chart wrapper
├── BarChartComponent.jsx    # Bar chart wrapper
├── DataViewComponent.jsx    # Simple 2-column data view
├── hooks/
│   └── useDashboardData.js  # Data fetching hooks
├── index.js                 # Export file
└── README.md               # This file
```

## 🎯 Components

### DashboardCard
Reusable card component for displaying metrics with icons and change indicators.

```jsx
<DashboardCard
  title="Total Users"
  value="1,234"
  change={12.5}
  icon="bi-people"
  color="primary"
  size="medium"
  width="250px"
/>
```

**Props:**
- `title` (string): Card title
- `value` (string): Main value to display
- `change` (number): Percentage change (optional)
- `icon` (string): Bootstrap icon class
- `color` (string): Color theme (primary, success, warning, danger)
- `size` (string): Size variant (small, medium, large)
- `width` (string): Custom width

### PieChartComponent
Pie chart component with automatic percentage calculation.

```jsx
<PieChartComponent
  title="User Distribution"
  data={[
    { name: 'Active', value: 45, color: '#4ecdc4' },
    { name: 'Inactive', value: 30, color: '#ff6b6b' }
  ]}
  size="medium"
  loading={false}
/>
```

**Props:**
- `title` (string): Chart title
- `data` (array): Array of objects with name, value, color
- `size` (string): Size variant
- `loading` (boolean): Loading state

### BarChartComponent
Bar chart component for displaying categorical data.

```jsx
<BarChartComponent
  title="Monthly Revenue"
  xAxis="month"
  yAxis="revenue"
  data={[
    { month: 'Jan', revenue: 12000 },
    { month: 'Feb', revenue: 15000 }
  ]}
  size="medium"
  loading={false}
/>
```

**Props:**
- `title` (string): Chart title
- `data` (array): Array of data objects
- `xAxis` (string): Key for x-axis values
- `yAxis` (string): Key for y-axis values
- `size` (string): Size variant
- `loading` (boolean): Loading state

### DataViewComponent
Simple 2-column data view for name-value pairs.

```jsx
<DataViewComponent
  title="System Information"
  data={[
    { name: "Server Status", value: "Online" },
    { name: "Last Backup", value: "2 hours ago" }
  ]}
  loading={false}
/>
```

**Props:**
- `title` (string): Section title
- `data` (array): Array of objects with name and value
- `loading` (boolean): Loading state

## 🎨 Usage

### Basic Implementation
```jsx
import { CommonDashboard } from '../../../components/dashboard';

const MyDashboard = () => {
  return <CommonDashboard role="vendor" />;
};
```

### With Custom Layout
```jsx
import { 
  DashboardLayout, 
  DashboardCard, 
  PieChartComponent,
  BarChartComponent,
  DataViewComponent,
  useDashboardCards
} from '../../../components/dashboard';

const CustomDashboard = ({ role }) => {
  const { cards } = useDashboardCards(role);
  
  return (
    <DashboardLayout>
      <div className="custom-grid">
        {cards.map(card => (
          <DashboardCard key={card.id} {...card} />
        ))}
      </div>
    </DashboardLayout>
  );
};
```

## 📊 Data Structure

### Role-Based Data
Each role has specific data structure:

```javascript
{
  cards: [
    {
      id: 'unique_id',
      title: 'Card Title',
      value: 'Display Value',
      change: 12.5,
      icon: 'bi-icon-name',
      color: 'primary',
      width: 'auto'
    }
  ],
  pieCharts: [
    {
      id: 'chart_id',
      title: 'Chart Title',
      data: [
        { name: 'Label', value: 45, color: '#667eea' }
      ]
    }
  ],
  barCharts: [
    {
      id: 'bar_chart_id',
      title: 'Bar Chart Title',
      xAxis: 'x_key',
      yAxis: 'y_key',
      data: [
        { x_key: 'Label', y_key: 45 }
      ]
    }
  ],
  dataViews: [
    {
      id: 'view_id',
      title: 'View Title',
      data: [
        { name: 'Label', value: 'Value' }
      ]
    }
  ]
}
```

## 🎯 Roles

### SuperAdmin
- Total users, revenue, vendors, system health
- User distribution pie chart
- Monthly revenue bar chart
- System statistics and activities

### Vendor
- Total clients, revenue, licenses, approvals
- License distribution pie chart
- Monthly sales bar chart
- Client and license statistics

### Tenant
- Users created, company allocation, active plan, storage
- Resource usage pie chart
- User activity bar chart
- Subscription and usage details

### CA (Chartered Accountant)
- Total clients, earnings, audits, tasks
- Client types pie chart
- Audit performance bar chart
- Financial and work summary

## 🛠️ Customization

### Adding New Role
1. Add role data to `useDashboardData.js`
2. Create role-specific dashboard page
3. Pass role prop to `CommonDashboard`

### Custom Card Colors
Add new color variants in `DashboardCard.css`:
```css
.dashboard-card--custom .dashboard-card__icon {
  background: linear-gradient(135deg, #custom-color-1 0%, #custom-color-2 100%);
}
```

### Custom Chart Themes
Modify chart options in component files to customize tooltips, legends, and colors.

## 📱 Responsive Design

- **Desktop**: Full grid layout with all components
- **Tablet**: Stacked charts, adjusted card grid
- **Mobile**: Single column layout, optimized touch targets

## 🔄 Loading States

All components include skeleton loaders:
- Card skeletons for metrics
- Chart skeletons for visualizations
- Data view skeletons for tables

## 🚨 Error Handling

- Individual component error boundaries
- Graceful fallbacks for missing data
- User-friendly error messages

## 🎨 Styling

Uses CSS variables for theming:
```css
:root {
  --dashboard-primary: #667eea;
  --dashboard-success: #4ecdc4;
  --dashboard-warning: #ffa726;
  --dashboard-danger: #ff6b6b;
}
```

## 📦 Dependencies

- `chart.js`: Charting library
- `react-chartjs-2`: React wrapper for Chart.js
- `react`: Core React library

## 🚀 Performance

- Lazy loading for large datasets
- Memoized components to prevent re-renders
- Optimized chart rendering
- Efficient state management

## 🧪 Testing

Components are designed to be easily testable:
- Isolated component logic
- Mockable data hooks
- Props-based configuration
- No external dependencies in core logic
