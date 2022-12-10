import { Modal, notification, Typography } from 'antd';
import { useContext, useState } from 'react';

import { createEvent } from '../../api/calendar/calendar.api';
import { EventCreateRequest } from '../../api/calendar/calendar.dto';
import { CreateEventForm } from '../../components/CreateEventForm/CreateEventForm';
import { PlanitUserContext } from '../../contexts/PlanitUserContext';
import { PresetsContext } from '../../contexts/PresetsContext';
import { useUrlQuery } from '../../hooks/useUrlQuery';

export const CreateEventPage = () => {
  const { userDetails } = useContext(PlanitUserContext);
  const { createOrUpdatePreset } = useContext(PresetsContext);
  const query = useUrlQuery();

  const [formRerenderKey, setFormRerenderKey] = useState(0);

  const onEventSubmit = async (result: EventCreateRequest) => {
    try {
      const eventResponse = await createEvent(result);

      notification.success({
        message: (
          <div>
            Successfully created your event(s).{' '}
            <Typography.Link href={eventResponse.event_url} target="_blank">
              Preview it in Google Calendar
            </Typography.Link>
          </div>
        ),
        placement: 'bottom',
      });

      setFormRerenderKey((prev) => prev + 1);
    } catch (e) {
      console.error(e);
    }
  };

  return (
    <>
      {query.get('close_prompt') === 'true' && (
        <Modal
          open={true}
          closable={false}
          footer={null}
          centered
          title="Successfully signed up!"
          maskStyle={{ backgroundColor: 'white' }}
        >
          {`You can now close this page.`}
        </Modal>
      )}

      <CreateEventForm
        key={formRerenderKey}
        onSubmit={onEventSubmit}
        onSaveToPreset={createOrUpdatePreset}
        owner={userDetails?.ownerEmail || ''}
      />
    </>
  );
};
