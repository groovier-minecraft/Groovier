package commands.math

import com.ericlam.mc.groovier.command.CommandScript
import com.ericlam.mc.groovier.command.CommandArg
import groovy.transform.Field
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import services.MathService

import javax.inject.Inject



// showcase for multiple commands
// and using dependency injection for service


@Field @Inject MathService mathService

@CommandScript(description = 'minus two numbers')
void minus(CommandSender sender, @CommandArg('one') int a, @CommandArg('two') int b) {
    sender.sendMessage(TextComponent.fromLegacy("$a - $b = ${mathService.minus(a, b)}"))
}