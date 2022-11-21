import { Card, Layout } from 'antd';
import { Header, Content } from 'antd/lib/layout/layout';
import { useContext } from 'react';
import { Outlet } from 'react-router-dom';

import './App.css';
import GlobalNav from './components/GlobalNav/GlobalNav';
import { PlanitUserContext } from './contexts/PlanitUserContext';

function MainLayout() {
  const { isLoggedIn } = useContext(PlanitUserContext);

  return (
    <Layout style={{ backgroundColor: '#D8D8D8', height: '100%' }}>
      <Header
        style={{
          backgroundColor: '#FFFFFF',
          boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.15)',
          height: '46px',
          zIndex: 1,
          display: isLoggedIn ? 'initial' : 'none',
        }}
      >
        <GlobalNav />
      </Header>
      <Layout style={{ padding: '56px 45px 0' }}>
        <Content>
          <Card
            style={{
              boxShadow: '0px 2px 4px rgba(0, 0, 0, 0.15)',
              height: '500px',
              maxWidth: '1000px',
              margin: 'auto',
            }}
            bodyStyle={{ height: '100%' }}
          >
            <Outlet />
          </Card>
        </Content>
      </Layout>
    </Layout>
  );
}

export default MainLayout;
