import 'package:event_bus/event_bus.dart';

import 'audio_status.dart';

EventBus eventBus = EventBus();

class HeadsetNotification {
  HeadsetStatus status;

  HeadsetNotification(this.status);
}
