from mapparse import getBlockBoundaries
from player import Player

class Game:
    def __init__(self):
        self.blocks = getBlockBoundaries()
        self.players = {}
        self.addPlayer(Player(1, 1, "Alice", (40.58393104493445, -74.08781943111359), self._findBlockForLoc((40.58393104493445, -74.08781943111359))))

    def addPlayer(self, player):
        self.players[player.id] = player

    def playerFromId(self, id):
        return self.players[id]

    #handles player1 attacking player2
    def processAttack(self, player1, player2):
        if player1.team != player2.team and player1.is_active():
            player2.disable()

    def updatePlayerLocation(self, player, loc):
        player.last_location = loc

    def processCapture(self, player):
        if player.is_active():
            cur_block = self._findBlockForPlayer(player)
            if cur_block:
                cur_block.updateControl(player)

    def _findBlockForPlayer(self, player):
        if player.last_block.contains(player.last_location):
            return player.last_block

        #player has changed blocks, do dumb search
        return self._findBlockForLoc(player.last_location)

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
                    'center' : [block.center[0], block.center[1]],
                    'control' : block.control
                }  for block in self.blocks
            }
        }
