import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import PushNotifier from 'react-native-push-notifier';

export default function App() {
  React.useEffect(() => {
    PushNotifier.showInfoPush(
      { title: 'title', body: 'body' },
      5346454,
      'default'
    );
  }, []);

  return (
    <View style={styles.container}>
      <Text>Hiii</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
