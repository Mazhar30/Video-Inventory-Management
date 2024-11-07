// api.js

const BASE_URL = 'http://localhost:8080';

// Function for user login
export const login = async (username, password) => {
    const response = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        body: JSON.stringify({ username, password }),
        headers: { 'Content-Type': 'application/json' }
    });

    if (!response.ok) {
        throw new Error('Login failed');
    }

    const data = await response.json();
 
    localStorage.setItem('token', data.token);
    return data.role;
};

// Function for user logout
export const logout = async () => {

    const token = localStorage.getItem('token');
    const response = await fetch(`${BASE_URL}/auth/logout`, {
        method: 'POST',
        headers: { 
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
        }
    });

    if (!response.ok) {
        throw new Error('Logout failed');
    }

    localStorage.removeItem('token');

    return response;
};

// Function for user registration
export const register = async (userData) => {
    const response = await fetch(`${BASE_URL}/user/register`, {
        method: 'POST',
        body: JSON.stringify(userData),
        headers: { 'Content-Type': 'application/json' }
    });

    if (!response.ok) {
        throw new Error('Registration failed');
    }

    return response;
};

export const getRole = async () => {
    const token = localStorage.getItem('token');
    const response = await fetch(`${BASE_URL}/auth/role`, {
        method: 'GET',
        headers: { 
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
        }
    });

    if (!response.ok) {
        throw new Error('Role fetching failed');
    }

    return response.text();
};

// Function for uploading videos
export const uploadVideo = async (videoData) => {
    const formData = new FormData();
    formData.append('file', videoData.file);
    formData.append('title', videoData.title); 
    formData.append('description', videoData.description);

    const token = localStorage.getItem('token');

    const response = await fetch(`${BASE_URL}/videos`, {
        method: 'POST',
        body: formData,
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        conditionalLogout(response);
        throw new Error('Video upload failed');
    }

    return response;
};

export const updateVideo = async (videoData) => {
    const formData = new FormData();
    formData.append('title', videoData.title);
    formData.append('description', videoData.description); 

    const token = localStorage.getItem('token');

    const response = await fetch(`${BASE_URL}/videos/${videoData.id}`, {
        method: 'PUT',
        body: formData,
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        conditionalLogout(response);
        throw new Error('Video update failed');
    }

    return response;
};

export const fetchActivityLogs = async () => {

    const token = localStorage.getItem('token');
    const response = await fetch(`${BASE_URL}/activity-log`, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`, 
        },
    });

    if (!response.ok) {
        conditionalLogout(response);
        throw new Error('Video upload failed');
    }

    return response.json();
};

export const fetchVideos = async () => {

    const token = localStorage.getItem('token');
    const response = await fetch(`${BASE_URL}/videos`, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        conditionalLogout(response);
        throw new Error('Video upload failed');
    }

    return response.json();
};

export const fetchVideoById = async (id) => {

    const token = localStorage.getItem('token');
    const response = await fetch(`${BASE_URL}/videos/${id}`, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        conditionalLogout(response);
        throw new Error('Video upload failed');
    }

    return response.json();
};

export const fetchUsers = async () => {

    const token = localStorage.getItem('token');
    const response = await fetch(`${BASE_URL}/user`, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        conditionalLogout(response);
        throw new Error('Fetching user failed');
    }

    return response.json();
};

export const assignVideoToUsers = async (id, assignedUsers) => {

    const token = localStorage.getItem('token');
    const response = await fetch(`${BASE_URL}/videos/${id}/assign`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json' 
        },
        body: JSON.stringify(assignedUsers)
    });

    if (!response.ok) {
        conditionalLogout(response);
        throw new Error('Video upload failed');
    }

    return response;
};

export const deleteVideo = async (id) => {

    const token = localStorage.getItem('token');
    const response = await fetch(`${BASE_URL}/videos/${id}`, {
        method: 'DELETE',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        conditionalLogout(response);
        throw new Error('Video upload failed');
    }

    return response;
};

export const getVideoStream = async (id) => {

    const token = localStorage.getItem('token');
    const response = await fetch(`${BASE_URL}/videos/video/${id}`, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
    

    if (!response.ok) {
        throw new Error('failed to fetch video stream');
    }

    return response.blob();
};

export const logUserActivity = async (userActivity) => {

    const token = localStorage.getItem('token');
    const response = await fetch(`${BASE_URL}/activity-log`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userActivity)
    });

    if (!response.ok) {
        throw new Error('Activity log update failed');
    }

    return response;
};

export const conditionalLogout = (response) => {
    if (response.status === 401 || response.status === 500) {
        logout();
        window.location.href = '/login';
    }
};
