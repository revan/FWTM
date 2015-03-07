from datetime import datetime

#minutes to wait when disabled
TIMEOUT_MINUTES = 10

class Player:
    def __init__(self, team, name, location):
        self.team = team
        self.name = name
        self.last_location = location
        self.activation_time = datetime.now()

    def disable(self):
        self.activation_time = datetime.now() + datetime.timedelta(minutes=TIMEOUT_MINUTES)

    def is_active(self):
        return datetime.now() > self.activaion_time
