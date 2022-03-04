import { Button, message } from 'antd';
import React, { useState } from 'react';
import { ping } from '../../api/api';
import TutorialOfferModal from '../tutorialOfferModal/TutorialOfferModal';

const Overview: React.FC = () => {
  const [isTutorialOfferModalVisible, setIsTutorialOfferModalVisible] = useState(false);

  return (
    <div>
      <Button onClick={e => {
        ping().then(
          res => message.success(res),
          err => message.error("Axios error")
        );
      }}>
        Ping backend
      </Button>
      <Button type="link" onClick={() => { setIsTutorialOfferModalVisible(true) }}>
          OfferModal
            </Button>
        <TutorialOfferModal
          isModalVisible={isTutorialOfferModalVisible}
          setIsTutorialOfferModalVisible={setIsTutorialOfferModalVisible}
        />
    </div>

  )
};

export default Overview;