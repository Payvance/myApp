import { useState, useEffect } from "react";
import "./CustomDatePicker.css";

const CustomDatePicker = ({
  selectedDate,
  onDateChange,
  onClose,
  minDate,
  maxDate,
}) => {
  const [currentDate, setCurrentDate] = useState(
    selectedDate ? new Date(selectedDate) : new Date()
  );
  const [view, setView] = useState("days");
  const [yearRange, setYearRange] = useState({
    start: Math.floor(currentDate.getFullYear() / 12) * 12,
    end: Math.floor(currentDate.getFullYear() / 12) * 12 + 11,
  });

  useEffect(() => {
    if (selectedDate) {
      setCurrentDate(new Date(selectedDate));
    }
  }, [selectedDate]);

  // Function to get the week number of the year (1-52/53)
  const getWeekNumber = (date) => {
    const d = new Date(date);
    d.setHours(0, 0, 0, 0);
    d.setDate(d.getDate() + 3 - ((d.getDay() + 6) % 7));

    const week1 = new Date(d.getFullYear(), 0, 4);
    return (
      1 +
      Math.round(((d - week1) / 86400000 - 3 + ((week1.getDay() + 6) % 7)) / 7)
    );
  };

  const daysInMonth = (month, year) => new Date(year, month + 1, 0).getDate();
  const firstDayOfMonth = (month, year) => new Date(year, month, 1).getDay();

  const handleDateSelect = (date) => {
    onDateChange(date);
    onClose();
  };

  const handleMonthSelect = (month) => {
    setCurrentDate(new Date(currentDate.getFullYear(), month, 1));
    setView("years");
  };

  const handleYearSelect = (year) => {
    setCurrentDate(new Date(year, currentDate.getMonth(), 1));
    setView("days");
  };

  const navigateMonth = (direction) => {
    setCurrentDate(
      new Date(currentDate.getFullYear(), currentDate.getMonth() + direction, 1)
    );
  };

  const navigateYearRange = (direction) => {
    const range = direction === "next" ? 12 : -12;
    setYearRange({
      start: yearRange.start + range,
      end: yearRange.end + range,
    });
  };

  const renderDaysView = () => {
    const month = currentDate.getMonth();
    const year = currentDate.getFullYear();
    const daysCount = daysInMonth(month, year);
    const firstDay = firstDayOfMonth(month, year);

    const weeks = [];
    let week = [];
    let dayCounter = 1;
    let currentWeekNumber = null;

    // Fill the first week with empty slots if needed
    for (let i = 0; i < firstDay; i++) {
      week.push(<td key={`empty-${i}`} className="empty-day"></td>);
    }

    // Fill the calendar with days
    while (dayCounter <= daysCount) {
      while (week.length < 7 && dayCounter <= daysCount) {
        const date = new Date(year, month, dayCounter);
        const dateStr = `${year}-${String(month + 1).padStart(2, "0")}-${String(
          dayCounter
        ).padStart(2, "0")}`;
        const isDisabled =
          (minDate && new Date(dateStr) < new Date(minDate)) ||
          (maxDate && new Date(dateStr) > new Date(maxDate));

        // Calculate week number for the first day of each week
        if (week.length === 0) {
          currentWeekNumber = getWeekNumber(date);
        }

        // Check if this date is the selected date
        const isSelected = selectedDate === dateStr;

        week.push(
          <td
            key={`day-${dayCounter}`}
            className={`day ${isDisabled ? "disabled" : ""} ${
              isSelected ? "selected" : ""
            }`}
            onClick={!isDisabled ? () => handleDateSelect(dateStr) : null}
          >
            {dayCounter}
          </td>
        );
        dayCounter++;
      }

      // Add week number at the start of each row
      weeks.push(
        <tr key={`week-${currentWeekNumber}-${dayCounter}`}>
          <td className="week-number">{currentWeekNumber}</td>
          {week}
        </tr>
      );
      week = [];
    }

    return (
      <div className="days-view">
        <div className="date-picker-header">
          <button
            type="button"
            onClick={() => navigateMonth(-1)}
            className="navigation-button"
          >
            &lt;
          </button>
          <button
            type="button"
            onClick={() => setView("months")}
            className="month-year-button"
          >
            {currentDate.toLocaleString("default", { month: "short" })} -{" "}
            {currentDate.getFullYear()}
          </button>

          <button
            type="button"
            onClick={() => navigateMonth(1)}
            className="navigation-button"
          >
            &gt;
          </button>
        </div>
        <div className="Calender-table">
          <table className="Calender-table">
            <thead>
              <tr>
                <th
                  style={{ backgroundColor: "var(--sidbar-name-hover)" }}
                ></th>
                <th style={{ backgroundColor: "#cf5d5d" }}>Su</th>
                <th>Mo</th>
                <th>Tu</th>
                <th>We</th>
                <th>Th</th>
                <th>Fr</th>
                <th>Sa</th>
              </tr>
            </thead>
            <tbody>{weeks}</tbody>
          </table>
        </div>
        <div className="date-picker-footer ">
          <button
            className="month-year-button"
            style={{ borderRadius: "20px" }}
            onClick={() =>
              handleDateSelect(
                maxDate
                  ? maxDate
                  : minDate
                  ? minDate
                  : new Date().toISOString().split("T")[0]
              )
            }
          >
            Today
          </button>
          &nbsp;
          <button
            type="button"
            className="month-year-button"
            style={{ borderRadius: "20px" }}
            onClick={() => onDateChange("")} // Clears the input field
          >
            Clear
          </button>
        </div>
      </div>
    );
  };

  const renderMonthsView = () => {
    const months = [
      "Jan",
      "Feb",
      "Mar",
      "Apr",
      "May",
      "Jun",
      "Jul",
      "Aug",
      "Sep",
      "Oct",
      "Nov",
      "Dec",
    ];

    return (
      <div className="months-view">
        <div className="date-picker-header">
          <button
            type="button"
            onClick={() => setView("days")}
            className="month-year-button"
          >
            &lt; Back
          </button>

          <button
            type="button"
            onClick={() => setView("years")}
            className="month-year-button"
          >
            {currentDate.getFullYear()}
          </button>
        </div>
        <div className="months-grid">
          {months.map((month, index) => (
            <button
              type="button"
              key={month}
              className={`month-button ${
                currentDate.getMonth() === index ? "selected" : ""
              }`}
              onClick={() => handleMonthSelect(index)}
            >
              {month}
            </button>
          ))}
        </div>
      </div>
    );
  };

  const renderYearsView = () => {
    const years = [];
    for (let year = yearRange.start; year <= yearRange.end; year++) {
      years.push(year);
    }

    return (
      <div className="years-view">
        <div className="date-picker-header">
          <button
            type="button"
            onClick={() => navigateYearRange("prev")}
            className="navigation-button"
          >
            &lt;
          </button>
          <button
            type="button"
            onClick={() => setView("days")}
            className="month-year-button"
          >
            {yearRange.start} - {yearRange.end}
          </button>
          <button
            type="button"
            onClick={() => navigateYearRange("next")}
            className="navigation-button"
          >
            &gt;
          </button>
        </div>
        <div className="years-grid">
          {years.map((year) => (
            <button
              type="button"
              key={year}
              className={`year-button ${
                year === currentDate.getFullYear() ? "selected" : ""
              }`}
              onClick={() => handleYearSelect(year)}
            >
              {year}
            </button>
          ))}
        </div>
      </div>
    );
  };

  return (
    <div className="custom-date-picker">
      {view === "days" && renderDaysView()}
      {view === "months" && renderMonthsView()}
      {view === "years" && renderYearsView()}
    </div>
  );
};

export default CustomDatePicker;
// Export the CustomDatePicker component for use in other parts of the application