from mapparse import getBlockBoundaries

class Game:
    def __init__(self):
        self.blocks = getBlockBoundaries()
        self.players = set()

    def addPlayer(self, player):
        self.players.add(player)
