import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axios'; // axios instance

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const response = await api.post('/auth/login', {
        email,
        password
      });

      // ✅ Correct field names from backend
      const { accessToken, tokenType } = response.data;

      if (accessToken) {
        localStorage.setItem('token', `${tokenType} ${accessToken}`);
        navigate('/dashboard');
      } else {
        setError("No token received from server.");
      }
    } catch (err) {
      console.error("Login Error:", err);
      setError(err.response?.data?.message || "Invalid email or password.");
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', marginTop: '50px' }}>
      <form
        onSubmit={handleLogin}
        style={{ border: '1px solid #ccc', padding: '20px', borderRadius: '8px', width: '320px' }}
      >
        <h2>Login</h2>

        {error && <p style={{ color: 'red', fontSize: '14px' }}>{error}</p>}

        <div style={{ marginBottom: '10px' }}>
          <label style={{ display: 'block' }}>Email: </label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label style={{ display: 'block' }}>Password: </label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            style={{ width: '100%', padding: '8px', boxSizing: 'border-box' }}
          />
        </div>

        <button
          type="submit"
          style={{
            width: '100%',
            cursor: 'pointer',
            padding: '10px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px'
          }}
        >
          Sign In
        </button>

        <p style={{ marginTop: '15px' }}>
          Don't have an account? <Link to="/register">Register here</Link>
        </p>
      </form>
    </div>
  );
}