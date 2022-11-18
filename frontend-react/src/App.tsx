import { Route, Routes } from 'react-router-dom';

import './App.css';
import MainLayout from './MainLayout';
import { ProtectedRoute } from './components/ProtectedRoute/ProtectedRoute';
import { CreateEventPage } from './routes/CreateEventPage/CreateEventPage';
import { ManagePresetsPage } from './routes/ManagePresetsPage/ManagePresetsPage';
import SignIn from './routes/SignInPage/SignIn';
import { SignUpPage } from './routes/SignInPage/SignUpPage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<SignIn />} />
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
          path="manage-presets"
          element={
            <ProtectedRoute>
              <ManagePresetsPage />
            </ProtectedRoute>
          }
        />
      </Route>
    </Routes>
  );
}

export default App;
