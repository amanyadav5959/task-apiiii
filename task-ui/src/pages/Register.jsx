import React, { useState } from 'react'; // Added React import for safety
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axios';

export default function Register() {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false); // Added loading state for better UX
  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();

    // Simple frontend validation to match backend rules (min 8 chars)
    if (formData.password.length < 8) {
      alert("Password must be at least 8 characters long.");
      return;
    }

    setLoading(true);
    try {
      // Sending data to http://localhost:8080/api/v1/auth/register
      const response = await api.post('/auth/register', {
        email: formData.email,
        password: formData.password
      });

      console.log("Registration Response:", response.data);
      alert("Registration Successful! Please login.");
      navigate('/login');
    } catch (error) {
      console.error("Registration failed:", error);
      // Backend usually sends error details in error.response.data
      const message = error.response?.data?.message || "Error creating account. User might already exist.";
      alert(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', marginTop: '50px', fontFamily: 'Arial, sans-serif' }}>
      <form onSubmit={handleRegister} style={{ border: '1px solid #ccc', padding: '30px', borderRadius: '12px', width: '350px', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }}>
        <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>Create Account</h2>

        <div style={{ marginBottom: '15px' }}>
          <label style={{ display: 'block', marginBottom: '5px' }}>Email:</label>
          <input
            type="email"
            placeholder="example@gmail.com"
            style={{ width: '100%', padding: '10px', borderRadius: '4px', border: '1px solid #ddd', boxSizing: 'border-box' }}
            required
            value={formData.email}
            onChange={(e) => setFormData({...formData, email: e.target.value})}
          />
        </div>

        <div style={{ marginBottom: '20px' }}>
          <label style={{ display: 'block', marginBottom: '5px' }}>Password:</label>
          <input
            type="password"
            placeholder="Min 8 characters"
            style={{ width: '100%', padding: '10px', borderRadius: '4px', border: '1px solid #ddd', boxSizing: 'border-box' }}
            required
            value={formData.password}
            onChange={(e) => setFormData({...formData, password: e.target.value})}
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          style={{
            width: '100%',
            padding: '12px',
            backgroundColor: loading ? '#ccc' : '#28a745',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer',
            fontSize: '16px'
          }}
        >
          {loading ? 'Registering...' : 'Register'}
        </button>

        <p style={{ textAlign: 'center', marginTop: '15px' }}>
          Already have an account? <Link to="/login" style={{ color: '#007bff', textDecoration: 'none' }}>Login here</Link>
        </p>
      </form>
    </div>
  );
}