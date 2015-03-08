from mapparse import getBlockBoundaries
from player import Player

class Game:
    def __init__(self):
        self.blocks = getBlockBoundaries()
        self.players = set()
        self.addPlayer(Player(1, "Alice", (40.58393104493445, -74.08781943111359), self._findBlockForLoc((40.58393104493445, -74.08781943111359))))

    def addPlayer(self, player):
        self.players.add(player)

    #handles player1 attacking player2
    def processAttack(self, player1, player2):
        if player1.team != player2.team and player1.is_active():
            player2.disable()

    #handles player's capture ping
    def processCapture(self, player):
        if player.is_active():
            _findBlockForPlayer(player).updateControl(player)

    def _findBlockForPlayer(self, player):
        if player.last_block.contains(player.last_location):
            return player.last_block

        #player has changed blocks, do dumb search
        return _findBlockForLoc(player.last_location)

    def _findBlockForLoc(self, loc):
        for block in self.blocks:
            if block.contains(loc):
                return block

    def getState(self):
        return {
            'players' : [player.getState() for player in self.players],
            'blocks' : {
                block.id : {
                    'boundaries' : [[boundary[0], boundary[1]] for boundary in block.boundaries],
                    'center' : block.center,
                    'control' : block.control
                }  for block in self.blocks
            }
        }
