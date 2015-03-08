from mapparse import getBlockBoundaries
from player import Player

class Game:
    def __init__(self):
        self.blocks = getBlockBoundaries(50)
        self.players = {}
        self.team_sum = 0

    def addPlayer(self, name, loc):
        id = len(self.players)

        team = 1
        if self.team_sum > 0:
            team = -1
        self.team_sum += team
        
        player = Player(id, team, name, loc, self._findBlockForLoc(loc))
        self.players[player.id] = player
        return player

    def playerFromId(self, id):
        if id in self.players:
            return self.players[id]

    #handles player1 attacking player2
    def processAttack(self, player1, player2):
        if player1.team != player2.team and player1.is_active():
            player2.disable()
            return True
        return False

    def updatePlayerLocation(self, player, loc):
        player.last_location = loc

    def processCapture(self, player):
        if player.is_active():
            cur_block = self._findBlockForPlayer(player)
            if cur_block:
                cur_block.updateControl(player)

    def _findBlockForPlayer(self, player):
        if player.last_block and player.last_block.contains(player.last_location):
            return player.last_block

        #player has changed blocks, do dumb search
        return self._findBlockForLoc(player.last_location)

    def _findBlockForLoc(self, loc):
        for block in self.blocks:
            if block.contains(loc):
                return block

    def getState(self):
        return {
            'players' : [self.players[id].getState() for id in self.players],
            'blocks' : {
                block.id : {
                    'boundaries' : [[boundary[0], boundary[1]] for boundary in block.boundaries],
                    'center' : [block.center[0], block.center[1]],
                    'control' : block.control
                }  for block in self.blocks
            }
        }
