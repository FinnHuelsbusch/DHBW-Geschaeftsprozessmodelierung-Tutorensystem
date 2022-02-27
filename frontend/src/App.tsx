import React, { useState } from 'react';
import './App.css';
import { Button, Layout, message } from 'antd';
import { Content, Header } from 'antd/lib/layout/layout';
import Navigation from './components/navigation/Navigation';
import { ping } from './api/api';
import { User } from './types/User';

const App: React.FC = () => {

  const [loggedUser, setLoggedUser] = useState<User | undefined>(undefined);

  return (
    <div className="App">
      <Layout>

        <Header>
          <Navigation />
        </Header>

        <Content style={{ minHeight: '100vh' }}>
          <div style={{ textAlign: 'center', marginTop: '36px' }}>
            <Button onClick={e => {
              ping().then(
                res => message.success(res),
                err => message.error("Axios error")
              );
            }}>
              Ping backend
            </Button>
          </div>
        </Content>

      </Layout>
    </div>
  );
}

export default App;
