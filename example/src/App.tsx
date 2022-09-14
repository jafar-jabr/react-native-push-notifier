import * as React from 'react';

import { StyleSheet, View } from 'react-native';
import PushNotifier from 'react-native-push-notifier';

export default function App() {
  React.useEffect(() => {
    PushNotifier.showActionPush({}, '7');
  }, []);

  return <View style={styles.container}></View>;
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
