import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

const Dashboard = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  // States for the "Create" form
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  // 1. READ: Fetch tasks on load (with Auth check)
  useEffect(() => {
    const fetchTasks = async () => {
      const token = localStorage.getItem('token');

      // Safety Check: If no token, send user back to login
      if (!token) {
        navigate('/login');
        return;
      }

      try {
        const response = await api.get('/tasks');
        setTasks(response.data);
      } catch (error) {
        console.error("Error fetching tasks:", error);
        if (error.response?.status === 401 || error.response?.status === 403) {
          alert("Session expired. Please login again.");
          handleLogout();
        }
      } finally {
        setLoading(false);
      }
    };
    fetchTasks();
  }, [navigate]);

  // 2. CREATE: Add a new task
  const handleAddTask = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post('/tasks', {
        title,
        description,
        status: 'PENDING'
      });
      setTasks([...tasks, response.data]);
      setTitle('');
      setDescription('');
    } catch (error) {
      alert("Failed to create task. Your session might be invalid.");
    }
  };

  // 3. UPDATE: Toggle status
  const handleToggleStatus = async (task) => {
    const updatedStatus = task.status === 'PENDING' ? 'COMPLETED' : 'PENDING';
    try {
      const response = await api.put(`/tasks/${task.id}`, {
        ...task,
        status: updatedStatus
      });
      setTasks(tasks.map(t => t.id === task.id ? response.data : t));
    } catch (error) {
      console.error("Update failed:", error);
    }
  };

  // 4. DELETE: Remove a task
  const handleDelete = async (id) => {
    if (window.confirm("Are you sure you want to delete this task?")) {
      try {
        await api.delete(`/tasks/${id}`);
        setTasks(tasks.filter(task => task.id !== id));
      } catch (error) {
        console.error("Delete failed:", error);
      }
    }
  };

  // LOGOUT FUNCTION
  const handleLogout = () => {
    localStorage.removeItem('token'); // Clear the token
    navigate('/login'); // Send to login page
  };

  if (loading) return <p style={{ textAlign: 'center', marginTop: '50px' }}>Loading tasks...</p>;

  return (
    <div style={{ padding: '40px', maxWidth: '800px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h1>Task Dashboard</h1>
        <button onClick={handleLogout} style={{ padding: '8px 15px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
          Logout
        </button>
      </div>

      {/* CREATE FORM SECTION */}
      <form onSubmit={handleAddTask} style={{ background: '#f8f9fa', padding: '20px', borderRadius: '8px', marginBottom: '30px', border: '1px solid #dee2e6' }}>
        <h3>Create New Task</h3>
        <input
          type="text"
          placeholder="Task Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
          style={{ display: 'block', width: '100%', marginBottom: '10px', padding: '10px', boxSizing: 'border-box' }}
        />
        <textarea
          placeholder="Description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          style={{ display: 'block', width: '100%', marginBottom: '10px', padding: '10px', boxSizing: 'border-box', height: '80px' }}
        />
        <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#28a745', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer', width: '100%' }}>
          + Add Task
        </button>
      </form>

      <hr />

      {/* READ & UPDATE/DELETE SECTION */}
      {tasks.length === 0 ? (
        <p style={{ textAlign: 'center', color: '#666' }}>No tasks found. Start by creating one!</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {tasks.map((task) => (
            <li key={task.id} style={{ marginBottom: '15px', padding: '15px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: task.status === 'COMPLETED' ? '#f0fff0' : '#fff' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <h3 style={{ margin: '0 0 5px 0', textDecoration: task.status === 'COMPLETED' ? 'line-through' : 'none' }}>
                    {task.title}
                  </h3>
                  <p style={{ margin: '0 0 10px 0', color: '#555' }}>{task.description}</p>
                  <span>Status: <strong style={{ color: task.status === 'COMPLETED' ? 'green' : 'orange' }}>{task.status}</strong></span>
                </div>

                <div style={{ display: 'flex', gap: '10px' }}>
                  <button onClick={() => handleToggleStatus(task)} style={{ padding: '6px 12px', cursor: 'pointer', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px' }}>
                    {task.status === 'PENDING' ? 'Done' : 'Undo'}
                  </button>
                  <button onClick={() => handleDelete(task.id)} style={{ padding: '6px 12px', cursor: 'pointer', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '4px' }}>
                    Delete
                  </button>
                </div>
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default Dashboard;