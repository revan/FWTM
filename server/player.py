from datetime import datetime

#minutes to wait when disabled
TIMEOUT_MINUTES = 3

class Player:
    def __init__(self, team, name, location, block):
        self.team = team
        self.name = name
        self.last_location = location
        self.last_block = block
        self.activation_time = datetime.now()

    def disable(self):
        self.activation_time = datetime.now() + datetime.timedelta(minutes=TIMEOUT_MINUTES)

    def is_active(self):
        return datetime.now() > self.activation_time

    def getState(self):
        return {
            'team' : self.team,
            'name' : self.name,
            'location' : [self.last_location[0], self.last_location[1]],
            'is_active' : self.is_active()
        }
