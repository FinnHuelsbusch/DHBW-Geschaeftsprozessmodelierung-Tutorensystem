import { Button, Col, message, Modal, PageHeader, Row, Space, Tag, Typography } from 'antd';
import Paragraph from 'antd/lib/typography/Paragraph';
import Title from 'antd/lib/typography/Title';
import React, { useContext, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getRequestError, getTutorial, markTutorial, participateInTutorial, removeParticipationInTutorial, unmarkTutorial } from '../../api/api';
import { AuthContext } from '../../context/UserContext';
import { AppRoutes } from '../../types/AppRoutes';
import { getErrorMessageString } from '../../types/RequestError';
import { Tutorial } from '../../types/Tutorial';
import { formatDate } from '../../utils/DateTimeHandling';
import { StarOutlined, StarFilled, WarningOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons'
import './TutorialDetails.scss';
import { UserRole } from '../../types/User';
import TutorialDeleteModal from './TutorialDeleteModal';
import TutorialCreateModal from '../tutorialCreateModal/TutorialCreateModal';

const TutorialDetails: React.FC = () => {

    const [isTutorialDeleteModalVisible, setIsTutorialDeleteModalVisible] = useState(false);
    const [isTutorialCreateModalVisible, setIsTutorialCreateModalVisible] = useState(false);
    const authContext = useContext(AuthContext);
    const navigate = useNavigate();

    const [tutorial, setTutorial] = useState<Tutorial | undefined>();
    const [loading, setLoading] = useState(false);

    // get value of tutorialId URL parameter
    const { tutorialId } = useParams();

    const fetchTutorial = () => {
        if (tutorialId) {
            getTutorial(parseInt(tutorialId))
                .then(tutorial => setTutorial(tutorial))
                .catch(err => {
                    const reqErr = getRequestError(err);
                    message.error(getErrorMessageString(reqErr.errorCode));
                });
        }
    };

    useEffect(() => {
        fetchTutorial();
    }, [tutorialId]);




    const TutorialDetailsPage = (tutorial: Tutorial) => {

        const getDetailsRow = (label: string, content: any) => {
            return (
                <Row>
                    <Col className="view-only-label" flex={"0 0 200px"}>{label}</Col>
                    <Col className="view-only-value" flex={"1 1 200px"}>{content}</Col>
                </Row>
            );
        };

        const handleTutorialActionClick = (action: () => any) => {
            if (!tutorial) return;
            if (!authContext.loggedUser) {
                // must be logged in, redirect to login page
                navigate(AppRoutes.Main.Subroutes.Login);
            } else {
                action();
            }
        };

        const onParticipateClick = () => {
            if (!tutorial) return;
            // if user is not logged in, redirect to login page
            if (tutorial.participates) {
                // show remove participation prompt
                Modal.confirm({
                    title: "Nicht mehr teilnehmen",
                    content: <div>
                        Hiermit melden Sie sich <b>verbindlich</b> von der Teilnahme am Tutorium '{tutorial.title}' ab.
                    </div>,
                    okText: 'Teilnahme entfernen (verbindlich)',
                    icon: <WarningOutlined color='red' />,
                    okButtonProps: { danger: true },
                    onOk() {
                        setLoading(true);
                        removeParticipationInTutorial(tutorial.id)
                            .then(res => {
                                message.success("Teilnahme wurde entfernt");
                                setLoading(false);
                                setTutorial({ ...tutorial, participates: false });
                            }).catch(err => {
                                message.error(getErrorMessageString(getRequestError(err).errorCode));
                                setLoading(false);
                            });
                    }
                });
            } else {
                // show participate prompt
                Modal.confirm({
                    title: "Am Tutorium teilnehmen",
                    content: <div>
                        Hiermit melden Sie sich <b>verbindlich</b> zur Teilnahme am Tutorium '{tutorial.title}' an.
                    </div>,
                    okText: 'Teilnehmen (verbindlich)',
                    onOk() {
                        setLoading(true);
                        participateInTutorial(tutorial.id)
                            .then(res => {
                                message.success("Teilnahme erfolgreich");
                                setTutorial({ ...tutorial, participates: true });
                                setLoading(false);
                            }).catch(err => {
                                message.error(getErrorMessageString(getRequestError(err).errorCode));
                                setLoading(false);
                            });
                    }
                });
            }
        };

        const onMarkClick = () => {
            setLoading(true);
            if (tutorial.isMarked) {
                unmarkTutorial(tutorial.id)
                    .then(res => {
                        setTutorial({
                            ...tutorial,
                            isMarked: false
                        });
                        setLoading(false);
                    }).catch(err => {
                        const reqErr = getRequestError(err);
                        message.error(getErrorMessageString(reqErr.errorCode));
                        setLoading(false);
                    });
            } else {
                markTutorial(tutorial.id)
                    .then(res => {
                        setTutorial({
                            ...tutorial,
                            isMarked: true
                        });
                        setLoading(false);
                    }).catch(err => {
                        const reqErr = getRequestError(err);
                        message.error(getErrorMessageString(reqErr.errorCode));
                        setLoading(false);
                    });
            }
        };

        const TutorialActions = () => {
            console.log(tutorial.holds);
            if (tutorial.holds) {
                // tutors' view: only show info that he/she is holding the tutorial
                return (
                    <Tag style={{
                        fontSize: '14px',
                        padding: '6px 12px'
                    }}>
                        Sie halten dieses Tutorium
                    </Tag>
                );
            } else if (authContext.hasRoles([UserRole.ROLE_DIRECTOR])) {
                // directors' view: only show delete button
                return (
                    <>
                    <Button
                        danger
                        disabled={loading}
                        onClick={e => onDeleteClick()}>
                        <DeleteOutlined /> Tutorium löschen
                    </Button>
                    <Button
                        
                        type='primary'
                        disabled={loading}
                        onClick={e => onEditClick()}>
                        <EditOutlined /> Tutorium bearbeiten
                    </Button>
                    
                    </>
                );
            } else {
                // public view: show mark and participate buttons
                return (
                    <Space wrap align='baseline'>
                        <Button
                            type='default'
                            disabled={loading}
                            onClick={e => handleTutorialActionClick(onMarkClick)}>
                            {tutorial.isMarked
                                ? <>
                                    <StarFilled style={{ color: '#ffd805' }} /> Vorgemerkt
                                </>
                                : <>
                                    <StarOutlined /> Vormerken
                                </>
                            }
                        </Button>
                        <Button
                            type='primary'
                            loading={loading}
                            danger={tutorial.participates ? true : false}
                            onClick={e => handleTutorialActionClick(onParticipateClick)}>
                            {tutorial.participates ? "Nicht mehr teilnehmen" : "Am Tutorium teilnehmen"}
                        </Button>
                    </Space>
                );
            }
        };

        const onDeleteClick = () => {
            setIsTutorialDeleteModalVisible(true);
        };

        const onEditClick = () => {
            setIsTutorialCreateModalVisible(true);
        };

        return (
            <>
                <PageHeader
                    ghost={false}
                    title={tutorial.title}
                    onBack={() => navigate(-1)}
                    extra={[TutorialActions()]}
                >
                    <Typography style={{ marginTop: '16px' }}>
                        <Title level={4}>Inhalt</Title>
                        <Paragraph>{tutorial.description}</Paragraph>

                        <Title level={4}>Details</Title>
                        <Paragraph>
                            {getDetailsRow("Gesamtumfang",
                                `${tutorial.durationMinutes} Minuten`)}
                            {getDetailsRow("Zeitraum",
                                `${formatDate(tutorial.start)} - ${formatDate(tutorial.end)}`)}
                            {getDetailsRow("Anzahl Teilnehmer",
                                tutorial.numberOfParticipants)}
                            {getDetailsRow(tutorial.tutors.length > 1 ? "Tutoren" : "Tutor",
                                tutorial.tutors.map(t => `${t.firstName} ${t.lastName}`)
                                    .reduce((prev, curr) => `${prev} ${curr}`))}
                        </Paragraph>

                        <Title level={4}>Studienrichtungen</Title>
                        <Paragraph>
                            Für folgende Studienrichtungen ist dieses Tutorium geeignet:
                        </Paragraph>
                        <Paragraph>
                            {tutorial.specialisationCourses.map(specialisationCourse => (
                                <Tag>{specialisationCourse.course.abbreviation} {specialisationCourse.abbreviation}</Tag>
                            ))}
                        </Paragraph>
                    </Typography>
                    <TutorialDeleteModal
                        isModalVisible={isTutorialDeleteModalVisible}
                        setIsTutorialDeleteModalVisible={(visible: boolean, deleted: boolean) =>{
                            setIsTutorialDeleteModalVisible(visible);
                           if(deleted){
                            navigate(-1);
                           }}}
                        tutorial={tutorial}                
                    />
                    <TutorialCreateModal 
                        isModalVisible={isTutorialCreateModalVisible} 
                        setIsTutorialCreateModalVisible={(visible: boolean, isUpdated: boolean) =>{
                            setIsTutorialCreateModalVisible(visible);
                           if(isUpdated){
                            fetchTutorial();
                           }}}
                        existingTutorial={tutorial}
                    />
                </PageHeader>
            </>
        );
    };

    return (
        <>
            {tutorial && TutorialDetailsPage(tutorial)}
        </>
    );
};

export default TutorialDetails;