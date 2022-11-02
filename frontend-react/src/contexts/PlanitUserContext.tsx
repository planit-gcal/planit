import { createContext, useMemo, useState } from 'react';

import { useLocalStorage } from '../hooks/useLocalStorage';

type State = {
  planitUserId: string | null;
  setPlanitUserId: (id: string | null) => void;
  ownerEmail: string | null;
  setOwnerEmail: (id: string | null) => void;
};

// todo rename

export const CountStateContext = createContext<State>({} as State);

type CountProviderProps = { children: React.ReactNode };
export const CountProvider = ({ children }: CountProviderProps) => {
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

  return <CountStateContext.Provider value={value}>{children}</CountStateContext.Provider>;
};
