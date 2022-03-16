import { Button, message } from 'antd';
import React, { useContext, useState } from 'react';
import { ping } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import TutorialCreateModal from '../tutorialCreateModal/TutorialCreateModal';
import TutorialOfferModal from '../tutorialOfferModal/TutorialOfferModal';
import TutorialRequestModal from '../tutorialRequestModal/TutorialRequestModal';

const Overview: React.FC = () => {
  const [isTutorialOfferModalVisible, setIsTutorialOfferModalVisible] = useState(false);
  const [isTutorialRequestModalVisible, setIsTutorialRequestModalVisible] = useState(false);
  const [isTutorialCreateModalVisible, setIsTutorialCreateModalVisible] = useState(false);
  const authContext = useContext(AuthContext);

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

      <Button type="link" disabled={authContext.loggedUser == undefined} onClick={() => { setIsTutorialRequestModalVisible(true) }}>
        RequestModal
      </Button>

      <TutorialRequestModal
        isModalVisible={isTutorialRequestModalVisible}
        setIsTutorialRequestModalVisible={setIsTutorialRequestModalVisible}
      />

      <Button type="link" onClick={() => { setIsTutorialCreateModalVisible(true) }}>
        CreateModal
      </Button>
      
      <TutorialCreateModal
        isModalVisible={isTutorialCreateModalVisible}
        setIsTutorialCreateModalVisible={setIsTutorialCreateModalVisible}
      />
    </div>

  )
};

export default Overview;