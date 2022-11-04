import { Route, Routes } from 'react-router-dom';

import './App.css';
import MainLayout from './MainLayout';
import { ProtectedRoute } from './components/ProtectedRoute/ProtectedRoute';
import { AccountSettings } from './routes/AccountSettings/AccountSettings';
import { CreateEventPage } from './routes/CreateEventPage/CreateEventPage';
import { ManagePresetsPage } from './routes/ManagePresetsPage/ManagePresetsPage';
import { SignInPage } from './routes/SignInPage/SignInPage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<SignInPage />} />
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
        <Route
          path="account-settings"
          element={
            <ProtectedRoute>
              <AccountSettings />
            </ProtectedRoute>
          }
        />
      </Route>
    </Routes>
  );
}

export default App;
