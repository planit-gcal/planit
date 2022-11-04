import { ReactElement, useContext } from 'react';
import { Navigate } from 'react-router-dom';

import { PlanitUserContext } from '../../contexts/PlanitUserContext';

type ProtectedRouteProps = React.PropsWithChildren;
export const ProtectedRoute = ({ children }: ProtectedRouteProps): ReactElement => {
  const { userDetails } = useContext(PlanitUserContext);

  const isLoggedIn = !!userDetails?.planitUserId;
  if (!isLoggedIn) {
    return <Navigate to="/" replace />;
  }

  return children as ReactElement;
};
