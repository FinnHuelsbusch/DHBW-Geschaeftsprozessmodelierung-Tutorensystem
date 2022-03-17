import { Button, Card, List, message } from 'antd';
import Paragraph from 'antd/lib/typography/Paragraph';
import React, { useState, useContext } from 'react';
import { ping } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import { UserRole } from '../../types/User';
import TutorialCreateModal from '../tutorialCreateModal/TutorialCreateModal';
import TutorialOfferModal from '../tutorialOfferModal/TutorialOfferModal';
import TutorialRequestModal from '../tutorialRequestModal/TutorialRequestModal';

const Overview: React.FC = () => {

    const [isTutorialOfferModalVisible, setIsTutorialOfferModalVisible] = useState(false);
    const [isTutorialRequestModalVisible, setIsTutorialRequestModalVisible] = useState(false);
    const [isTutorialCreateModalVisible, setIsTutorialCreateModalVisible] = useState(false);
    const authContext = useContext(AuthContext);

    type CardProps = {
        title: string,
        description: string,
        children: React.ReactNode,
    };

    const OverviewCard: React.FC<CardProps> = ({ title, description, children }) => {
        return (
            <Card
                style={{
                    height: '250px'
                }}
                title={title}
            >
                <Paragraph>
                    {description}
                </Paragraph>
                <div style={{
                    float: 'right',
                    position: 'absolute',
                    bottom: 16,
                    right: 16,
                }}>
                    {children}
                </div>
            </Card>
        );
    };

    const CardsData = [
        <OverviewCard
            title='Ping'
            description='Pingen Sie das Backend.'>
            <Button type='link' onClick={e => {
                ping().then(
                    res => message.success(res),
                    err => message.error("Axios error")
                );
            }}>
                Ping Backend
            </Button>
        </OverviewCard>,

        <OverviewCard
            title='Tutorium anbieten'
            description='Bieten Sie ein Tutorium an. Nur für Studenten möglich.'>
            <Button type="link" disabled={!authContext.hasRoles([UserRole.ROLE_STUDENT])} onClick={() => { setIsTutorialOfferModalVisible(true) }}>
                Tutorium anbieten
            </Button>
        </OverviewCard>,

        <OverviewCard
            title='Tutorium anfragen'
            description='Fragen Sie ein neues Tutorium an. Nur für Studenten möglich.'>
            <Button type="link" disabled={!authContext.hasRoles([UserRole.ROLE_STUDENT])} onClick={() => { setIsTutorialRequestModalVisible(true) }}>
                Tutorium anfragen
            </Button>
        </OverviewCard>,

        <OverviewCard
            title='Tutorium erstellen'
            description='Erstellen Sie ein Tutorium mit zugewiesenen Tutoren. Nur für Studiengangsleiter möglich.'>
            <Button type="link" disabled={!authContext.hasRoles([UserRole.ROLE_DIRECTOR])} onClick={() => { setIsTutorialCreateModalVisible(true) }}>
                Tutorium erstellen
            </Button>
        </OverviewCard>
    ];

    return (
        <>
            <List
                grid={{
                    gutter: 16,
                    xs: 1,
                    sm: 2,
                    md: 3,
                    lg: 4,
                    xl: 4,
                    xxl: 6,
                }}
                dataSource={CardsData}
                renderItem={item =>
                    <List.Item>{item}</List.Item>
                } />

            <TutorialOfferModal
                isModalVisible={isTutorialOfferModalVisible}
                setIsTutorialOfferModalVisible={setIsTutorialOfferModalVisible}
            />
            <TutorialCreateModal
                isModalVisible={isTutorialCreateModalVisible}
                setIsTutorialCreateModalVisible={setIsTutorialCreateModalVisible}
            />

            <TutorialRequestModal
                isModalVisible={isTutorialRequestModalVisible}
                setIsTutorialRequestModalVisible={setIsTutorialRequestModalVisible}
            />
        </>
    );
};

export default Overview;