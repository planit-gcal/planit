import { Modal, notification } from 'antd';
import { useContext } from 'react';

import { createEvent } from '../../api/calendar/calendar.api';
import { EventCreateRequest } from '../../api/calendar/calendar.dto';
import { createUserPreset, updateUserPreset } from '../../api/presets/presets.api';
import { CreateEventForm } from '../../components/CreateEventForm/CreateEventForm';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';
import { PresetsContext } from '../../contexts/PresetsContext';
import { useUrlQuery } from '../../hooks/useUrlQuery';

export const CreateEventPage = () => {
  const { userDetails } = useContext(PlanitUserContext);
  const { createOrUpdatePreset } = useContext(PresetsContext);
  const query = useUrlQuery();

  const onEventSubmit = async (result: EventCreateRequest) => {
    await createEvent(result);
  };

  return (
    <>
      {query.get('close_prompt') === 'true' && (
        <Modal open={true} closable={false} footer={null} centered title="Successfully signed up!">
          {`You can now close this page.`}
        </Modal>
      )}
      <CreateEventForm
        onSubmit={onEventSubmit}
        onSaveToPreset={createOrUpdatePreset}
        owner={userDetails?.ownerEmail || ''}
      />
    </>
  );
};
