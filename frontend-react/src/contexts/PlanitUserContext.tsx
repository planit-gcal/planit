import { createContext, useMemo } from 'react';

import { useLocalStorage } from '../hooks/useLocalStorage';

type UserDetails = {
  planitUserId?: string;
  ownerEmail?: string;
};

type State = {
  userDetails: UserDetails | null;
  setUserDetails: (userDetails: React.SetStateAction<UserDetails>) => void;
};

export const PlanitUserContext = createContext<State>({} as State);

type PlanitUserProviderProps = { children: React.ReactNode };
export const PlanitUserProvider = ({ children }: PlanitUserProviderProps) => {
  const [userDetails, setUserDetails] = useLocalStorage<UserDetails>('userDetails', {});

  const value = useMemo(
    () => ({
      userDetails,
      setUserDetails,
    }),
    [userDetails, setUserDetails]
  );

  return <PlanitUserContext.Provider value={value}>{children}</PlanitUserContext.Provider>;
};
