import { Button, message } from 'antd';
import React, { useState } from 'react';
import { ping } from '../../api/api';
import TutorialOfferModal from '../tutorialOfferModal/TutorialOfferModal';
import TutorialRequestModal from '../tutorialRequestModal/TutorialRequestModal';

const Overview: React.FC = () => {
  const [isTutorialOfferModalVisible, setIsTutorialOfferModalVisible] = useState(false);
  const [isTutorialRequestModalVisible, setIsTutorialRequestModalVisible] = useState(false);

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
      <Button type="link" onClick={() => setIsTutorialOfferModalVisible(true)}>
        OfferModal
      </Button>
      <TutorialOfferModal
        isModalVisible={isTutorialOfferModalVisible}
        setIsTutorialOfferModalVisible={setIsTutorialOfferModalVisible}
      />
      <Button type="link" onClick={() => { setIsTutorialRequestModalVisible(true) }}>
        RequestModal
      </Button>
      <TutorialRequestModal
        isModalVisible={isTutorialRequestModalVisible}
        setIsTutorialRequestModalVisible={setIsTutorialRequestModalVisible}
      />
    </div>

  )
};

export default Overview;