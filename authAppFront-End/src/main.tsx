import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import {BrowserRouter, Routes, Route} from "react-router";
import './index.css'
import App from './App.tsx'
import Login from './pages/Login.tsx';
import About from './pages/About.tsx';
import Services from './pages/Services.tsx';
import SignUp from './pages/Signup.tsx';
import RootLayout from './pages/RootLayout.tsx';
import { Dashboard } from './pages/Dashboard.tsx';

createRoot(document.getElementById('root')!).render(
  // <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<RootLayout />} >
          <Route index element={<App />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/services" element={<Services />} />
          <Route path="/about" element={<About />} />
          <Route path="/dashboard" element={<Dashboard />} />
        </Route>
      </Routes>
    </BrowserRouter>
  // </StrictMode>
)