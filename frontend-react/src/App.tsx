import { Route, Routes } from 'react-router-dom';

import './App.css';
import MainLayout from './MainLayout';
import { ProtectedRoute } from './components/ProtectedRoute/ProtectedRoute';
import { CreateEventPage } from './routes/CreateEventPage/CreateEventPage';
import { ManageAccountPage } from './routes/ManageAccountPage/ManageAccountPage';
import SignInPage from './routes/SignInPage/SignInPage';
import { SignUpPage } from './routes/SignInPage/SignUpPage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<SignInPage />} />
        <Route path="sign-up" element={<SignUpPage />} />
        <Route
          path="create-events"
          element={
            <ProtectedRoute>
              <CreateEventPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="manage-account"
          element={
            <ProtectedRoute>
              <ManageAccountPage />
            </ProtectedRoute>
          }
        />
      </Route>
    </Routes>
  );
}

export default App;
