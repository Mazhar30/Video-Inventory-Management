import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Navbar from './components/Layout/Navbar';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import UploadVideo from './components/Admin/UploadVideo';
import AssignVideos from './components/Admin/AssignVideos';
import ActivityLog from './components/Admin/ActivityLog';
import VideoManagement from './components/Admin/VideoManagement';
import AssignedVideos from './components/User/AssignedVideos';
import VideoPlayer from './components/User/VideoPlayer';
import Footer from './components/Layout/Footer';
import ProtectedRoute from './components/ProtectedRoute';
import { getRole, logout } from './api/api';

const App = () => {
    const [role, setRole] = useState(null);  // State to store user role

    // Fetch role when the app first loads or when it's updated
    useEffect(() => {
        const fetchRole = async () => {
            try {
                const userRole = await getRole();
                setRole(userRole); 
            } catch (error) {
                console.error("Error fetching role:", error);
            }
        };

        fetchRole();  // Fetch role when the component mounts
    }, []);  // Empty dependency array ensures it runs only on initial mount

    const handleLogin = (userRole) => {
        setRole(userRole); 
    };

    const handleLogout = () => {
        logout();
        setRole(null);
    };

    return (
        <Router>
            <Navbar role={role} onLogout={handleLogout} />
            <Routes>

                 {/* Public Routes */}
                <Route path="/login" element={<Login onLogin={handleLogin} />} />
                <Route path="/register" element={<Register />} />

                {/* Admin Routes (Protected) */}
                <Route path="/admin/upload" element={
                    <ProtectedRoute role={role} setRole={setRole} requiredRole="ADMIN">
                        <UploadVideo />
                    </ProtectedRoute>
                } />
                <Route path="/admin/assign" element={
                    <ProtectedRoute role={role} setRole={setRole} requiredRole="ADMIN">
                        <AssignVideos />
                    </ProtectedRoute>
                } />
                <Route path="/admin/activity-log" element={
                    <ProtectedRoute role={role} setRole={setRole} requiredRole="ADMIN">
                        <ActivityLog />
                    </ProtectedRoute>
                } />
                <Route path="/admin/video-management" element={
                    <ProtectedRoute role={role} setRole={setRole} requiredRole="ADMIN">
                        <VideoManagement />
                    </ProtectedRoute>
                } />

                {/* User Routes (Protected) */}
                <Route path="/user/assigned-videos" element={
                    <ProtectedRoute role={role} setRole={setRole} requiredRole="USER">
                        <AssignedVideos />
                    </ProtectedRoute>
                } />
                <Route path="/user/video/:id" element={
                    <ProtectedRoute role={role} setRole={setRole} requiredRole="USER">
                        <VideoPlayer />
                    </ProtectedRoute>
                } />

                {/* Redirect all unknown routes */}
                <Route path="*" element={<Navigate to="/" />} />
            </Routes>
            <Footer />
        </Router>
    );
};

export default App;