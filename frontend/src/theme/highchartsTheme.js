import Highcharts from "highcharts";

export const applyHighchartsTheme = () => {
  Highcharts.setOptions({
    chart: {
      style: {
        fontFamily: "Inter, sans-serif"
      }
    },
    colors: [
      "#4F46E5", // Indigo
      "#22C55E", // Green
      "#EF4444", // Red
      "#F59E0B", // Amber
      "#06B6D4", // Cyan
      "#8B5CF6"  // Purple
    ],
    title: {
      style: {
        fontWeight: "600",
        fontSize: "16px"
      }
    },
    tooltip: {
      borderRadius: 8,
      shadow: false
    },
    credits: {
      enabled: false
    }
  });
};