import { createContext, useMemo, useState } from 'react';

import { useLocalStorage } from '../hooks/useLocalStorage';

type State = {
  planitUserId: string | null;
  setPlanitUserId: (id: string | null) => void;
  ownerEmail: string | null;
  setOwnerEmail: (id: string | null) => void;
};

export const PlanitUserContext = createContext<State>({} as State);

type PlanitUserProviderProps = { children: React.ReactNode };
export const PlanitUserProvider = ({ children }: PlanitUserProviderProps) => {
  const [planitUserId, setPlanitUserId] = useLocalStorage<string | null>('planitUserId', null);
  const [ownerEmail, setOwnerEmail] = useState<string | null>(null);

  const value = useMemo(
    () => ({
      planitUserId,
      setPlanitUserId,
      ownerEmail,
      setOwnerEmail,
    }),
    [planitUserId, setPlanitUserId, ownerEmail, setOwnerEmail]
  );

  return <PlanitUserContext.Provider value={value}>{children}</PlanitUserContext.Provider>;
};
