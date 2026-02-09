import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { Provider } from "react-redux"; 
import App from "./App.jsx";
import store from "./redux/store"; 
import "./index.css";
import { ThemeProvider } from './context/ThemeContext.jsx'
import "./styles/formLayout.css";


createRoot(document.getElementById("root")).render(
  <StrictMode>
    <Provider store={store}>
      <ThemeProvider>
        <App />
      </ThemeProvider>
    </Provider>
  </StrictMode>
);
