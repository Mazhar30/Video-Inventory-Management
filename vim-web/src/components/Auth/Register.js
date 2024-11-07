// src/components/Register.js
import React, { useState } from 'react';
import { register } from '../../api/api';
import '../../static/css/Register.css';

const Register = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setMessage('');

        if (password !== confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        try {
            const encodedPassword = btoa(password); // basic encoding
            await register({ username: username, password: encodedPassword });
            setMessage('Registration successful. You can now log in.');
        } catch (err) {
            setError('Registration failed. Please try again.');
        }
    };

    return (
        <div className="register-container">
            <h2>Register</h2>
            <form onSubmit={handleSubmit} className="register-form">
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                    className="register-input"
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    className="register-input"
                />
                <input
                    type="password"
                    placeholder="Confirm Password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                    className="register-input"
                />
                <button type="submit" className="register-button">Register</button>
            </form>
            {error && <p className="register-error">{error}</p>}
            {message && <p className="register-message">{message}</p>}
        </div>
    );
};

export default Register;
